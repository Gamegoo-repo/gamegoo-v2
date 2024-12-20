package com.gamegoo.gamegoo_v2.integration.chat;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.service.ChatFacadeService;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class ChatFacadeServiceTest {

    @Autowired
    private ChatFacadeService chatFacadeService;

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
    @DisplayName("특정 회원과 채팅 시작")
    class StartChatroomByMemberIdTest {

        @DisplayName("실패: 상대 회원을 찾을 수 없는 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenTargetMemberNotFound() {
            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, 10000L))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @DisplayName("실패: 대상 회원으로 본인 id를 입력한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenTargetMemberIsSelf() {
            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, member.getId()))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(ErrorCode._BAD_REQUEST.getMessage());
        }

        @DisplayName("내가 상대를 차단한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenTargetMemberIsBlockedByMe() {
            // given
            blockMember(member, targetMember);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, targetMember.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_CHAT_TARGET_IS_BLOCKED.getMessage());
        }

        @DisplayName("성공: 기존 채팅방이 존재히는 경우 해당 채팅방에 입장 처리 및 최근 메시지 내역을 조회해야 한다.")
        @Test
        void startChatroomByMemberIdSucceedsWhenExistingChatroom() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            // when
            EnterChatroomResponse response = chatFacadeService.startChatroomByMemberId(member, targetMember.getId());

            // then
            // lastViewDate 업데이트 검증
            MemberChatroom memberChatroom = memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(),
                    chatroom.getId()).get();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);

            // response 검증
            assertEnterChatroomResponse(response, chatroom, targetMember);
        }

        @DisplayName("성공: 기존 채팅방이 존재하지 않는 경우 새 채팅방이 생성되어야 한다.")
        @Test
        void startChatroomByMemberIdSucceedsWhenNoExistingChatroom() {
            // when
            EnterChatroomResponse response = chatFacadeService.startChatroomByMemberId(member, targetMember.getId());

            // then
            // 생성된 엔티티 검증
            Chatroom chatroom = chatroomRepository.findChatroomByMemberIds(member.getId(), targetMember.getId()).get();
            MemberChatroom memberChatroom = memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(),
                    chatroom.getId()).get();

            assertThat(memberChatroom.getLastViewDate()).isNull();
            assertThat(memberChatroom.getLastJoinDate()).isNull();

            // response 검증
            assertEnterChatroomResponse(response, chatroom, targetMember);
        }

        @DisplayName("실패: 기존 채팅방이 존재하지 않으며 상대가 나를 차단한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenNoExistingChatroomAndBlockedByTarget() {
            // given
            blockMember(targetMember, member);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, targetMember.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_BLOCKED_BY_CHAT_TARGET.getMessage());
        }

        @DisplayName("실패: 기존 채팅방이 존재하지 않으며 상대가 탈퇴한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenNoExistingChatroomAndTargetIsBlind() {
            // given
            blindMember(targetMember);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, targetMember.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_USER_DEACTIVATED.getMessage());
        }

    }

    private void assertEnterChatroomResponse(EnterChatroomResponse response, Chatroom chatroom, Member targetMember) {
        assertThat(response.getUuid()).isEqualTo(chatroom.getUuid());
        assertThat(response.getMemberId()).isEqualTo(targetMember.getId());
        assertThat(response.getGameName()).isEqualTo(targetMember.getGameName());
        assertThat(response.getMemberProfileImg()).isEqualTo(targetMember.getProfileImage());
        assertThat(response.isFriend()).isFalse();
        assertThat(response.isBlind()).isFalse();
        assertThat(response.isBlocked()).isFalse();
        assertThat(response.getFriendRequestMemberId()).isNull();
        assertThat(response.getSystem()).isNull();
        assertThat(response.getChatMessageListResponse().getChatMessageList()).isEmpty();
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
