package com.gamegoo.gamegoo_v2.integration.chat;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.board.domain.Board;
import com.gamegoo.gamegoo_v2.board.repository.BoardRepository;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.service.ChatFacadeService;
import com.gamegoo.gamegoo_v2.exception.BoardException;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;

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
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private MemberService memberService;

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

        @DisplayName("실패: 내가 상대를 차단한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenTargetMemberIsBlockedByMe() {
            // given
            blockMember(member, targetMember);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, targetMember.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_IS_BLOCKED.getMessage());
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
            assertThat(response.getSystem()).isNull();
        }

        @DisplayName("성공: 기존 채팅방이 존재하지 않는 경우 새 채팅방 생성 후 입장 처리되어야 한다.")
        @Test
        void startChatroomByMemberIdSucceedsWhenNoExistingChatroom() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            EnterChatroomResponse response = chatFacadeService.startChatroomByMemberId(member, targetMember.getId());

            // then
            // 생성된 엔티티 검증
            Chatroom chatroom = chatroomRepository.findChatroomByMemberIds(member.getId(), targetMember.getId()).get();
            MemberChatroom memberChatroom = memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(),
                    chatroom.getId()).get();

            assertThat(memberChatroom.getLastViewDate()).isAfter(now);
            assertThat(memberChatroom.getLastJoinDate()).isNull();

            // response 검증
            assertEnterChatroomResponse(response, chatroom, targetMember);
            assertThat(response.getSystem()).isNull();
        }

        @DisplayName("실패: 기존 채팅방이 존재하지 않으며 상대가 나를 차단한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenNoExistingChatroomAndBlockedByTarget() {
            // given
            blockMember(targetMember, member);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, targetMember.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_BLOCKED_BY_TARGET.getMessage());
        }

        @DisplayName("실패: 기존 채팅방이 존재하지 않으며 상대가 탈퇴한 경우 예외가 발생한다.")
        @Test
        void startChatroomByMemberId_shouldThrownWhenNoExistingChatroomAndTargetIsBlind() {
            // given
            blindMember(targetMember);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByMemberId(member, targetMember.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_DEACTIVATED.getMessage());
        }

    }

    @Nested
    @DisplayName("특정 글을 통한 채팅방 시작")
    class StartChatroomByBoardIdTest {

        @DisplayName("실패: 해당 글을 찾을 수 없는 경우 예외가 발생한다.")
        @Test
        void startChatroomByBoardId_shouldThrownWhenBoardNotFound() {
            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByBoardId(member, 10000L))
                    .isInstanceOf(BoardException.class)
                    .hasMessage(ErrorCode.BOARD_NOT_FOUND.getMessage());
        }

        @DisplayName("실패: 게시글 작성자를 찾을 수 없는 경우 예외가 발생한다.")
        @Test
        void startChatroomByBoardId_shouldThrownWhenMemberNotFound() {
            // given
            Board board = createBoard(targetMember);

            willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND))
                    .given(memberService).findMember(targetMember.getId());

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByBoardId(member, board.getId()))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @DisplayName("실패: 게시글 작성자가 본인인 경우 예외가 발생한다.")
        @Test
        void startChatroomByBoardId_shouldThrownWhenWriterIsSelf() {
            // given
            Board board = createBoard(member);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByBoardId(member, board.getId()))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(ErrorCode._BAD_REQUEST.getMessage());
        }

        @DisplayName("실패: 게시글 작성자가 탈퇴한 경우 예외가 발생한다.")
        @Test
        void startChatroomByBoardId_shouldThrownWhenWriterIsBlind() {
            // given
            Board board = createBoard(targetMember);

            blindMember(targetMember);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByBoardId(member, board.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_DEACTIVATED.getMessage());
        }

        @DisplayName("실패: 상대가 나를 차단한 경우 예외가 발생한다.")
        @Test
        void startChatroomByBoardId_shouldThrownWhenNoExistingChatroomAndBlockedByTarget() {
            // given
            Board board = createBoard(targetMember);

            blockMember(targetMember, member);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByBoardId(member, board.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_BLOCKED_BY_TARGET.getMessage());
        }

        @DisplayName("실패: 내가 상대를 차단한 경우 예외가 발생한다.")
        @Test
        void startChatroomByBoardId_shouldThrownWhenTargetIsBlocked() {
            // given
            Board board = createBoard(targetMember);

            blockMember(member, targetMember);

            // when // then
            assertThatThrownBy(() -> chatFacadeService.startChatroomByBoardId(member, board.getId()))
                    .isInstanceOf(ChatException.class)
                    .hasMessage(ErrorCode.CHAT_START_FAILED_TARGET_IS_BLOCKED.getMessage());
        }

        @DisplayName("성공: 기존 채팅방에 퇴장한 상태인 경우 해당 채팅방에 입장 처리 및 최근 메시지 내역을 조회해야 한다. systemFlag로는 1을 반환해야 한다.")
        @Test
        void startChatroomByBoardIdSucceedsWhenExitedExistingChatroom() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, null);
            createMemberChatroom(targetMember, chatroom, null);

            Board board = createBoard(targetMember);

            // when
            EnterChatroomResponse response = chatFacadeService.startChatroomByBoardId(member, board.getId());

            // then
            // lastViewDate 업데이트 검증
            MemberChatroom memberChatroom = memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(),
                    chatroom.getId()).get();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);

            // response 검증
            assertEnterChatroomResponse(response, chatroom, targetMember);
            assertThat(response.getSystem().getFlag()).isEqualTo(1);
            assertThat(response.getSystem().getBoardId()).isEqualTo(board.getId());
        }

        @DisplayName("성공: 기존 채팅방에 입장한 상태인 경우 해당 채팅방에 입장 처리 및 최근 메시지 내역을 조회해야 한다. systemFlag로는 2를 반환해야 한다.")
        @Test
        void startChatroomByBoardIdSucceedsWhenExistingChatroom() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Chatroom chatroom = createChatroom();
            createMemberChatroom(member, chatroom, now.minusDays(1));
            createMemberChatroom(targetMember, chatroom, null);

            Board board = createBoard(targetMember);

            // when
            EnterChatroomResponse response = chatFacadeService.startChatroomByBoardId(member, board.getId());

            // then
            // lastViewDate 업데이트 검증
            MemberChatroom memberChatroom = memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(),
                    chatroom.getId()).get();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);

            // response 검증
            assertEnterChatroomResponse(response, chatroom, targetMember);
            assertThat(response.getSystem().getFlag()).isEqualTo(2);
            assertThat(response.getSystem().getBoardId()).isEqualTo(board.getId());
        }

        @DisplayName("성공: 기존 채팅방이 존재하지 않는 경우 새 채팅방 생성 후 입장 처리되어야 한다. systemFlag로는 1을 반환해야 한다.")
        @Test
        void startChatroomByBoardIdSucceedsWhenNoExistingChatroom() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Board board = createBoard(targetMember);

            // when
            EnterChatroomResponse response = chatFacadeService.startChatroomByBoardId(member, board.getId());

            // then
            // lastViewDate 업데이트 검증
            Chatroom chatroom = chatroomRepository.findChatroomByMemberIds(member.getId(), targetMember.getId()).get();
            MemberChatroom memberChatroom = memberChatroomRepository.findByMemberIdAndChatroomId(member.getId(),
                    chatroom.getId()).get();
            assertThat(memberChatroom.getLastViewDate()).isAfter(now);

            // response 검증
            assertEnterChatroomResponse(response, chatroom, targetMember);
            assertThat(response.getSystem().getFlag()).isEqualTo(1);
            assertThat(response.getSystem().getBoardId()).isEqualTo(board.getId());
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
