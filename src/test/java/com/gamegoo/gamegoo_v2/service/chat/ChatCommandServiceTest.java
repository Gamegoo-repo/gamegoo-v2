package com.gamegoo.gamegoo_v2.service.chat;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.board.domain.Board;
import com.gamegoo.gamegoo_v2.board.repository.BoardRepository;
import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.domain.SystemMessageType;
import com.gamegoo.gamegoo_v2.chat.dto.request.SystemFlagRequest;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.service.ChatCommandService;
import com.gamegoo.gamegoo_v2.config.AsyncConfig;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import com.gamegoo.gamegoo_v2.socket.SocketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@Import(AsyncConfig.class)
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
    private BoardRepository boardRepository;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoBean
    private SocketService socketService;

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
        boardRepository.deleteAllInBatch();
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
                    .hasMessage(ErrorCode.CHAT_START_FAILED_BLOCKED_BY_TARGET.getMessage());
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
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_DEACTIVATED.getMessage());
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
            assertThat(memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(), chatroom.getId()))
                    .isPresent();
            assertThat(memberChatroomRepository.findByMemberIdAndChatroomId(targetMember.getId(), chatroom.getId()))
                    .isPresent();
        }

    }

    @Nested
    @DisplayName("시스템 메시지 등록 처리")
    class ProcessSystemMessagesTest {

        @DisplayName("실패: 해당 게시글을 찾을 수 없는 경우 예외가 발생한다.")
        @Test
        void processSystemMessages_shouldThrownWhenBoardNotFound() {
            // given
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            SystemFlagRequest request = SystemFlagRequest.builder()
                    .boardId(10000L)
                    .flag(1)
                    .build();

            // when // then
            assertThatThrownBy(() -> chatCommandService.processSystemMessages(request, member, targetMember, chatroom))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.ADD_BOARD_SYSTEM_CHAT_FAILED.getMessage());
        }

        @DisplayName("실패: system member를 찾을 수 없는 경우 예외가 발생한다.")
        @Test
        void processSystemMessages_shouldThrownWhenSystemMemberIsNotFound() {
            // given
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            Board board = createBoard(targetMember);

            SystemFlagRequest request = SystemFlagRequest.builder()
                    .boardId(board.getId())
                    .flag(1)
                    .build();

            // when // then
            assertThatThrownBy(() -> chatCommandService.processSystemMessages(request, member, targetMember, chatroom))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.SYSTEM_MEMBER_NOT_FOUND.getMessage());
        }

        @DisplayName("성공: 기존 lastJoinDate가 null인 경우 시스템 메시지 저장, lastJoinDate 업데이트 및 socket 이벤트가 발생해야 한다.")
        @Test
        void processSystemMessagesSucceedsWhenLastJoinDateIsNull() {
            // given
            Member systemMember = createMember("sytemMember@gmail.com", "systemMember");
            given(memberRepository.findById(0L)).willReturn(Optional.of(systemMember));

            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            Board board = createBoard(targetMember);

            SystemFlagRequest request = SystemFlagRequest.builder()
                    .boardId(board.getId())
                    .flag(1)
                    .build();

            doNothing().when(socketService).joinSocketToChatroom(any(Long.class), any(String.class));

            // when
            chatCommandService.processSystemMessages(request, member, targetMember, chatroom);

            // then
            // 시스템 메시지 생성 되었는지 검증
            List<Chat> systemMessages = chatRepository.findByChatroomIdAndFromMemberId(chatroom.getId(),
                    systemMember.getId());
            assertThat(systemMessages).hasSize(2);

            Chat memberSystemMessage = systemMessages.stream()
                    .filter(chat -> chat.getToMember().getId().equals(member.getId()))
                    .findFirst()
                    .orElseThrow();
            Chat targetSystemMessage = systemMessages.stream()
                    .filter(chat -> chat.getToMember().getId().equals(targetMember.getId()))
                    .findFirst()
                    .orElseThrow();
            assertThat(memberSystemMessage.getContents()).isEqualTo(SystemMessageType.of(1).getMessage());
            assertThat(targetSystemMessage.getContents()).isEqualTo(SystemMessageType.of(3).getMessage());

            // lastJoinDate 업데이트 되었는지 검증
            MemberChatroom memberChatroom = memberChatroomRepository
                    .findByMemberIdAndChatroomId(member.getId(), chatroom.getId())
                    .orElseThrow();
            MemberChatroom targetMemberChatroom = memberChatroomRepository
                    .findByMemberIdAndChatroomId(targetMember.getId(), chatroom.getId())
                    .orElseThrow();

            assertThat(memberChatroom.getLastJoinDate()).isNotNull();
            assertThat(targetMemberChatroom.getLastJoinDate()).isNotNull();

            // event로 인해 socketService 메소드가 호출되었는지 검증
            await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
                verify(socketService, times(2)).joinSocketToChatroom(any(Long.class), any(String.class));
            });
        }

        @DisplayName("성공: lastJoinDate가 null이 아닌 경우 시스템 메시지만 저장되어야 한다.")
        @Test
        void processSystemMessagesSucceedsWhenLastJoinDateIsNotNull() {
            // given
            Member systemMember = createMember("sytemMember@gmail.com", "systemMember");
            given(memberRepository.findById(0L)).willReturn(Optional.of(systemMember));

            Chatroom chatroom = createChatroom();
            LocalDateTime lastJoinDate = LocalDateTime.now().minusMinutes(10);
            createMemberChatroom(member, chatroom, lastJoinDate);
            createMemberChatroom(targetMember, chatroom, lastJoinDate);

            Board board = createBoard(targetMember);

            SystemFlagRequest request = SystemFlagRequest.builder()
                    .boardId(board.getId())
                    .flag(2)
                    .build();

            // when
            chatCommandService.processSystemMessages(request, member, targetMember, chatroom);

            // then
            List<Chat> systemMessages = chatRepository.findByChatroomIdAndFromMemberId(chatroom.getId(),
                    systemMember.getId());
            assertThat(systemMessages).hasSize(2);

            Chat memberSystemMessage = systemMessages.stream()
                    .filter(chat -> chat.getToMember().getId().equals(member.getId()))
                    .findFirst()
                    .orElseThrow();
            Chat targetSystemMessage = systemMessages.stream()
                    .filter(chat -> chat.getToMember().getId().equals(targetMember.getId()))
                    .findFirst()
                    .orElseThrow();
            assertThat(memberSystemMessage.getContents()).isEqualTo(SystemMessageType.of(2).getMessage());
            assertThat(targetSystemMessage.getContents()).isEqualTo(SystemMessageType.of(3).getMessage());

            // lastJoinDate 검증
            MemberChatroom memberChatroom = memberChatroomRepository
                    .findByMemberIdAndChatroomId(member.getId(), chatroom.getId())
                    .orElseThrow();
            MemberChatroom targetMemberChatroom = memberChatroomRepository
                    .findByMemberIdAndChatroomId(targetMember.getId(), chatroom.getId())
                    .orElseThrow();

            assertThat(memberChatroom.getLastJoinDate()).isCloseTo(lastJoinDate, within(1, ChronoUnit.SECONDS));
            assertThat(targetMemberChatroom.getLastJoinDate()).isCloseTo(lastJoinDate, within(1, ChronoUnit.SECONDS));
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

    private Board createBoard(Member member) {
        return boardRepository.save(Board.builder()
                .member(member)
                .mode(1)
                .mainPosition(1)
                .subPosition(2)
                .wantPosition(3)
                .mike(true)
                .content("content")
                .boardProfileImage(1)
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
