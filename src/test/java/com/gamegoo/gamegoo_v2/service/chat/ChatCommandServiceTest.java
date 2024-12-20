package com.gamegoo.gamegoo_v2.service.chat;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.service.ChatCommandService;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class ChatCommandServiceTest {

    @Autowired
    private ChatCommandService chatCommandService;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private MemberChatroomRepository memberChatroomRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Member targetMember;

    @BeforeEach
    void setUp() {
        member = createMember("test@gmail.com", "member");
        targetMember = createMember("target@gmail.com", "targetMember");
    }

    @AfterEach
    void tearDown() {
        chatRepository.deleteAllInBatch();
        memberChatroomRepository.deleteAllInBatch();
        chatroomRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("기존 채팅방 입장")
    class EnterExistingChatroomTest {

        @DisplayName("성공: 채팅방에서 퇴장한 상태인 경우 lastViewDate가 업데이트 되어야 한다.")
        @Test
        void enterExistingChatroomSucceedsWhenExited() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            // when
            MemberChatroom memberChatroom = chatCommandService.enterExistingChatroom(member, targetMember, chatroom);

            // then
            assertThat(memberChatroom.getLastViewDate()).isNotNull();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);
            assertThat(memberChatroom.getLastJoinDate()).isNull();
        }

        @DisplayName("성공: 채팅방에서 퇴장하지 않은 경우 상대가 나를 차단했어도 입장 가능하며 lastViewDate가 업데이트 되어야 한다.")
        @Test
        void enterExistingChatroomSucceedsWhenNotExitedAndBlockedByTarget() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, now.minusMinutes(10));
            createMemberChatroom(targetMember, chatroom, null);

            blockMember(targetMember, member);

            // when
            MemberChatroom memberChatroom = chatCommandService.enterExistingChatroom(member, targetMember, chatroom);

            // then
            assertThat(memberChatroom.getLastViewDate()).isNotNull();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);
            assertThat(memberChatroom.getLastJoinDate()).isCloseTo(now.minusMinutes(10), within(1, ChronoUnit.SECONDS));
        }

        @DisplayName("성공: 채팅방에서 퇴장하지 않은 경우 상대가 탈퇴했어도 입장 가능하며 lastViewDate가 업데이트 되어야 한다.")
        @Test
        void enterExistingChatroomSucceedsWhenNotExistedAndTargetIsBlind() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, now.minusMinutes(10));
            createMemberChatroom(targetMember, chatroom, null);

            // 상대 회원 탈퇴 처리
            blindMember(targetMember);

            // when
            MemberChatroom memberChatroom = chatCommandService.enterExistingChatroom(member, targetMember, chatroom);

            // then
            assertThat(memberChatroom.getLastViewDate()).isNotNull();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);
            assertThat(memberChatroom.getLastJoinDate()).isCloseTo(now.minusMinutes(10), within(1, ChronoUnit.SECONDS));
        }

        @DisplayName("실패: 채팅방을 찾을 수 없거나 해당 회원의 채팅방이 아닌 경우 예외가 발생한다.")
        @Test
        void enterExistingChatroom_shouldThrownWhenChatroomNotFound() {
            // when // then
            assertThatThrownBy(() -> chatCommandService.enterExistingChatroom(member, targetMember, createChatroom()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHATROOM_ACCESS_DENIED.getMessage());
        }

        @DisplayName("실패: 채팅방을 퇴장한 상태이며 상대가 나를 차단한 경우 예외가 발생한다.")
        @Test
        void enterExistingChatroom_shouldThrownWhenMemberIsBlockedByTarget() {
            // given
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            // 상대가 나를 차단
            blockMember(targetMember, member);

            // when // then
            assertThatThrownBy(() -> chatCommandService.enterExistingChatroom(member, targetMember, chatroom))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_BLOCKED_BY_CHAT_TARGET.getMessage());
        }

        @DisplayName("실패: 채팅방을 퇴장한 상태이며 상대가 탈퇴한 경우 예외가 발생한다.")
        @Test
        void enterExistingChatroom_shouldThrownWhenTargetMemberIsBlind() {
            // given
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            // 상대 회원 탈퇴 처리
            blindMember(targetMember);

            // when // then
            assertThatThrownBy(() -> chatCommandService.enterExistingChatroom(member, targetMember, chatroom))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_USER_DEACTIVATED.getMessage());
        }

    }

    @Nested
    @DisplayName("새로운 채팅방 생성")
    class CreateChatroomTest {

        @DisplayName("새로운 채팅방 생성 성공: chatroom과 memberChatroom 엔티티가 저장되어야 한다.")
        @Test
        void createChatroomSucceeds() {
            // when
            Chatroom chatroom = chatCommandService.createChatroom(member, targetMember);

            // then
            assertThat(chatroom).isNotNull();
            assertThat(memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(), chatroom.getId())).isPresent();
            assertThat(memberChatroomRepository.findByMemberIdAndChatroomId(targetMember.getId(), chatroom.getId())).isPresent();
        }

    }

    private Member createMember(String email, String gameName) {
        return memberRepository.save(Member.builder()
                .email(email)
                .password("testPassword")
                .profileImage(1)
                .loginType(LoginType.GENERAL)
                .gameName(gameName)
                .tag("TAG")
                .tier(Tier.IRON)
                .gameRank(0)
                .winRate(0.0)
                .gameCount(0)
                .isAgree(true)
                .build());
    }

    private Chatroom createChatroom() {
        return chatroomRepository.save(Chatroom.builder()
                .uuid(UUID.randomUUID().toString())
                .build());
    }

    private MemberChatroom createMemberChatroom(Member member, Chatroom chatroom, LocalDateTime lastJoinDate) {
        return memberChatroomRepository.save(MemberChatroom.builder()
                .chatroom(chatroom)
                .member(member)
                .lastViewDate(null)
                .lastJoinDate(lastJoinDate)
                .build());
    }

    private Block blockMember(Member member, Member targetMember) {
        return blockRepository.save(Block.create(member, targetMember));
    }

    private void blindMember(Member member) {
        member.updateBlind(true);
        memberRepository.save(member);
    }

}
