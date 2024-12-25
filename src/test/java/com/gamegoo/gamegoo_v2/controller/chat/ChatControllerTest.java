package com.gamegoo.gamegoo_v2.controller.chat;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.chat.controller.ChatController;
import com.gamegoo.gamegoo_v2.chat.dto.request.ChatCreateRequest;
import com.gamegoo.gamegoo_v2.chat.dto.request.SystemFlagRequest;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatCreateResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatMessageListResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatMessageResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse.SystemFlagResponse;
import com.gamegoo.gamegoo_v2.chat.service.ChatFacadeService;
import com.gamegoo.gamegoo_v2.controller.ControllerTestSupport;
import com.gamegoo.gamegoo_v2.utils.DateTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest extends ControllerTestSupport {

    @MockitoBean
    private ChatFacadeService chatFacadeService;

    private static final String API_URL_PREFIX = "/api/v2";
    private static final Long MEMBER_ID = 1L;
    private static final Long TARGET_MEMBER_ID = 2L;
    private static final Long TARGET_BOARD_ID = 1L;
    private static final String TARGET_CHATROOM_UUID = "test-uuid";
    private static final String TARGET_GAMENAME = "targetMember";

    @DisplayName("특정 회원과 채팅방 시작 성공: 채팅방 및 상대 회원 정보, 대화 내역이 반환된다.")
    @Test
    void startChatroomByMemberIdSucceeds() throws Exception {
        // given
        ChatMessageListResponse chatMessageListResponse = ChatMessageListResponse.builder()
                .chatMessageList(new ArrayList<>())
                .listSize(0)
                .hasNext(false)
                .nextCursor(null)
                .build();

        EnterChatroomResponse response = EnterChatroomResponse.builder()
                .memberId(TARGET_MEMBER_ID)
                .gameName(TARGET_GAMENAME)
                .memberProfileImg(1)
                .blind(false)
                .blocked(false)
                .friend(false)
                .friendRequestMemberId(null)
                .uuid(TARGET_CHATROOM_UUID)
                .system(null)
                .chatMessageListResponse(chatMessageListResponse)
                .build();

        given(chatFacadeService.startChatroomByMemberId(any(Member.class), eq(TARGET_MEMBER_ID)))
                .willReturn(response);

        // when // then
        mockMvc.perform(get(API_URL_PREFIX + "/chat/start/member/{memberId}", TARGET_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.chatMessageListResponse").isNotEmpty())
                .andExpect(jsonPath("$.data.memberId").value(TARGET_MEMBER_ID))
                .andExpect(jsonPath("$.data.gameName").value(TARGET_GAMENAME))
                .andExpect(jsonPath("$.data.memberProfileImg").value(1))
                .andExpect(jsonPath("$.data.blind").value(false))
                .andExpect(jsonPath("$.data.blocked").value(false))
                .andExpect(jsonPath("$.data.friend").value(false))
                .andExpect(jsonPath("$.data.friendRequestMemberId").isEmpty())
                .andExpect(jsonPath("$.data.uuid").value(TARGET_CHATROOM_UUID))
                .andExpect(jsonPath("$.data.system").isEmpty());
    }

    @DisplayName("특정 글을 통한 채팅방 시작 성공: 채팅방 및 상대 회원 정보, 시스템 메시지 정보, 대화 내역이 반환된다.")
    @Test
    void startChatroomByBoardIdSucceeds() throws Exception {
        // given
        ChatMessageListResponse chatMessageListResponse = ChatMessageListResponse.builder()
                .chatMessageList(new ArrayList<>())
                .listSize(0)
                .hasNext(false)
                .nextCursor(null)
                .build();

        SystemFlagResponse systemFlagResponse = SystemFlagResponse.builder()
                .flag(1)
                .boardId(TARGET_BOARD_ID)
                .build();

        EnterChatroomResponse response = EnterChatroomResponse.builder()
                .memberId(TARGET_MEMBER_ID)
                .gameName(TARGET_GAMENAME)
                .memberProfileImg(1)
                .blind(false)
                .blocked(false)
                .friend(false)
                .friendRequestMemberId(null)
                .uuid(TARGET_CHATROOM_UUID)
                .system(systemFlagResponse)
                .chatMessageListResponse(chatMessageListResponse)
                .build();

        given(chatFacadeService.startChatroomByBoardId(any(Member.class), eq(TARGET_BOARD_ID)))
                .willReturn(response);

        // when // then
        mockMvc.perform(get(API_URL_PREFIX + "/chat/start/board/{boardId}", TARGET_BOARD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.chatMessageListResponse").isNotEmpty())
                .andExpect(jsonPath("$.data.memberId").value(TARGET_MEMBER_ID))
                .andExpect(jsonPath("$.data.gameName").value(TARGET_GAMENAME))
                .andExpect(jsonPath("$.data.memberProfileImg").value(1))
                .andExpect(jsonPath("$.data.blind").value(false))
                .andExpect(jsonPath("$.data.blocked").value(false))
                .andExpect(jsonPath("$.data.friend").value(false))
                .andExpect(jsonPath("$.data.friendRequestMemberId").isEmpty())
                .andExpect(jsonPath("$.data.uuid").value(TARGET_CHATROOM_UUID))
                .andExpect(jsonPath("$.data.system").isNotEmpty())
                .andExpect(jsonPath("$.data.system.flag").value(1))
                .andExpect(jsonPath("$.data.system.boardId").value(TARGET_BOARD_ID));
    }

    @DisplayName("특정 채팅방 입장 성공: 채팅방 및 상대 회원 정보, 대화 내역이 반환된다.")
    @Test
    void enterChatroomSucceeds() throws Exception {
        // given
        ChatMessageListResponse chatMessageListResponse = ChatMessageListResponse.builder()
                .chatMessageList(new ArrayList<>())
                .listSize(0)
                .hasNext(false)
                .nextCursor(null)
                .build();

        EnterChatroomResponse response = EnterChatroomResponse.builder()
                .memberId(TARGET_MEMBER_ID)
                .gameName(TARGET_GAMENAME)
                .memberProfileImg(1)
                .blind(false)
                .blocked(false)
                .friend(false)
                .friendRequestMemberId(null)
                .uuid(TARGET_CHATROOM_UUID)
                .system(null)
                .chatMessageListResponse(chatMessageListResponse)
                .build();

        given(chatFacadeService.enterChatroomByUuid(any(Member.class), eq(TARGET_CHATROOM_UUID)))
                .willReturn(response);

        // when // then
        mockMvc.perform(get(API_URL_PREFIX + "/chat/{chatroomUuid}/enter", TARGET_CHATROOM_UUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.chatMessageListResponse").isNotEmpty())
                .andExpect(jsonPath("$.data.memberId").value(TARGET_MEMBER_ID))
                .andExpect(jsonPath("$.data.gameName").value(TARGET_GAMENAME))
                .andExpect(jsonPath("$.data.memberProfileImg").value(1))
                .andExpect(jsonPath("$.data.blind").value(false))
                .andExpect(jsonPath("$.data.blocked").value(false))
                .andExpect(jsonPath("$.data.friend").value(false))
                .andExpect(jsonPath("$.data.friendRequestMemberId").isEmpty())
                .andExpect(jsonPath("$.data.uuid").value(TARGET_CHATROOM_UUID))
                .andExpect(jsonPath("$.data.system").isEmpty());

    }

    @Nested
    @DisplayName("채팅 메시지 등록")
    class AddChatTest {

        @DisplayName("실패: message가 empty인 경우 에러 응답을 반환한다.")
        @Test
        void addChatFailedWhenMessageIsEmpty() throws Exception {
            // given
            ChatCreateRequest request = ChatCreateRequest.builder()
                    .message("")
                    .system(null)
                    .build();

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/chat/{chatroomUuid}", TARGET_CHATROOM_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.message").value("message는 필수 값 입니다."));
        }

        @DisplayName("실패: system flag가 null인 경우 에러 응답을 반환한다.")
        @Test
        void addChatFailedWhenFlagIsNull() throws Exception {
            // given
            SystemFlagRequest systemFlagRequest = SystemFlagRequest.builder()
                    .flag(null)
                    .boardId(TARGET_BOARD_ID)
                    .build();

            ChatCreateRequest request = ChatCreateRequest.builder()
                    .message("message")
                    .system(systemFlagRequest)
                    .build();

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/chat/{chatroomUuid}", TARGET_CHATROOM_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.message").value("flag는 필수 값 입니다."));
        }

        @DisplayName("살패: system flag가 1보다 작은 경우 에러 응답을 반환한다.")
        @Test
        void addChatFailedWhenFlagIsLessThan() throws Exception {
            // given
            SystemFlagRequest systemFlagRequest = SystemFlagRequest.builder()
                    .flag(0)
                    .boardId(TARGET_BOARD_ID)
                    .build();

            ChatCreateRequest request = ChatCreateRequest.builder()
                    .message("message")
                    .system(systemFlagRequest)
                    .build();

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/chat/{chatroomUuid}", TARGET_CHATROOM_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.message").value("flag는 1 이상의 값이어야 합니다."));
        }

        @DisplayName("살패: system flag가 2보다 큰 경우 에러 응답을 반환한다.")
        @Test
        void addChatFailedWhenFlagIsGreaterThan() throws Exception {
            // given
            SystemFlagRequest systemFlagRequest = SystemFlagRequest.builder()
                    .flag(3)
                    .boardId(TARGET_BOARD_ID)
                    .build();

            ChatCreateRequest request = ChatCreateRequest.builder()
                    .message("message")
                    .system(systemFlagRequest)
                    .build();

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/chat/{chatroomUuid}", TARGET_CHATROOM_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.message").value("flag는 2 이하의 값이어야 합니다."));
        }

        @DisplayName("실패: boardId가 null인 경우 에러 응답을 반환한다.")
        @Test
        void addChatFailedWhenBoardIdIsNull() throws Exception {
            // given
            SystemFlagRequest systemFlagRequest = SystemFlagRequest.builder()
                    .flag(1)
                    .boardId(null)
                    .build();

            ChatCreateRequest request = ChatCreateRequest.builder()
                    .message("message")
                    .system(systemFlagRequest)
                    .build();

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/chat/{chatroomUuid}", TARGET_CHATROOM_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.message").value("boardId는 필수 값 입니다."));
        }

        @DisplayName("성공: 생성된 메시지 정보를 반환한다.")
        @Test
        void addChatSucceeds() throws Exception {
            // given
            SystemFlagRequest systemFlagRequest = SystemFlagRequest.builder()
                    .flag(1)
                    .boardId(TARGET_BOARD_ID)
                    .build();

            ChatCreateRequest request = ChatCreateRequest.builder()
                    .message("message")
                    .system(systemFlagRequest)
                    .build();

            ChatCreateResponse response = ChatCreateResponse.builder()
                    .senderId(MEMBER_ID)
                    .senderName("member")
                    .senderProfileImg(1)
                    .message("message")
                    .createdAt(DateTimeUtil.toKSTString(LocalDateTime.now()))
                    .timestamp(123456789012L)
                    .build();

            given(chatFacadeService.
                    createChat(any(ChatCreateRequest.class), any(Member.class), eq(TARGET_CHATROOM_UUID)))
                    .willReturn(response);

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/chat/{chatroomUuid}", TARGET_CHATROOM_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.senderId").value(MEMBER_ID))
                    .andExpect(jsonPath("$.data.senderName").value("member"))
                    .andExpect(jsonPath("$.data.senderProfileImg").value(1))
                    .andExpect(jsonPath("$.data.message").value("message"))
                    .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.data.timestamp").value(123456789012L));
        }

    }

    @Nested
    @DisplayName("채팅 내역 조회")
    class GetChatMessagesTest {

        @DisplayName("성공: cursor가 없는 경우")
        @Test
        void getChatMessagesSucceedsWhenNoCursor() throws Exception {
            // given
            List<ChatMessageResponse> chatMessageResponseList = new ArrayList<>();
            ChatMessageListResponse response = ChatMessageListResponse.builder()
                    .chatMessageList(chatMessageResponseList)
                    .listSize(0)
                    .nextCursor(null)
                    .hasNext(false)
                    .build();

            given(chatFacadeService.getChatMessagesByCursor(any(Member.class), any(String.class), any()))
                    .willReturn(response);

            // when // then
            mockMvc.perform(get(API_URL_PREFIX + "/chat/{chatroomUuid}/messages", TARGET_CHATROOM_UUID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.chatMessageList").isArray())
                    .andExpect(jsonPath("$.data.listSize").isNumber())
                    .andExpect(jsonPath("$.data.hasNext").isBoolean());
        }

        @DisplayName("성공: cursor가 있는 경우")
        @Test
        void getChatMessagesSucceedsWithCursor() throws Exception {
            // given
            List<ChatMessageResponse> chatMessageResponseList = new ArrayList<>();
            ChatMessageListResponse response = ChatMessageListResponse.builder()
                    .chatMessageList(chatMessageResponseList)
                    .listSize(0)
                    .nextCursor(null)
                    .hasNext(false)
                    .build();

            given(chatFacadeService.getChatMessagesByCursor(any(Member.class), any(String.class), any()))
                    .willReturn(response);

            // when // then
            mockMvc.perform(get(API_URL_PREFIX + "/chat/{chatroomUuid}/messages", TARGET_CHATROOM_UUID)
                            .param("cursor", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.chatMessageList").isArray())
                    .andExpect(jsonPath("$.data.listSize").isNumber())
                    .andExpect(jsonPath("$.data.hasNext").isBoolean());
        }

        @DisplayName("실패: cursor가 음수인 경우")
        @Test
        void getChatMessagesFailedWhenCursorIsNegative() throws Exception {
            // when // then
            mockMvc.perform(get(API_URL_PREFIX + "/chat/{chatroomUuid}/messages", TARGET_CHATROOM_UUID)
                            .param("cursor", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("커서는 1 이상의 값이어야 합니다."));
        }

    }

    @DisplayName("안읽은 채팅방 uuid 조회 성공")
    @Test
    void GetUnreadChatroomUuidSucceeds() throws Exception {
        // given
        List<String> response = List.of();

        given(chatFacadeService.getUnreadChatroomUuids(any(Member.class))).willReturn(response);

        // when // then
        mockMvc.perform(get(API_URL_PREFIX + "/chat/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("채팅방 나가기 성공")
    @Test
    void ExitChatroomSucceeds() throws Exception {
        // given
        String response = "채팅방 나가기 성공";

        given(chatFacadeService.exitChatroom(any(Member.class), any(String.class))).willReturn(response);

        // when // then
        mockMvc.perform(patch(API_URL_PREFIX + "/chat/{chatroomUuid}/exit", TARGET_CHATROOM_UUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("채팅방 나가기 성공"));
    }

}
