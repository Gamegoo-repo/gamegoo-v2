package com.gamegoo.gamegoo_v2.controller.chat;

import com.gamegoo.gamegoo_v2.chat.controller.ChatController;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatMessageListResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.chat.service.ChatFacadeService;
import com.gamegoo.gamegoo_v2.controller.ControllerTestSupport;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest extends ControllerTestSupport {

    @MockitoBean
    private ChatFacadeService chatFacadeService;

    private static final String API_URL_PREFIX = "/api/v2";
    private static final Long TARGET_MEMBER_ID = 2L;
    private static final String TARGET_CHATROOM_UUID = "test-uuid";
    private static final String TARGET_GAMENAME = "targetMember";

    @Nested
    @DisplayName("특정 회원과 채팅방 시작")
    class StartChatroomByMemberIdTest {

        @DisplayName("성공: 채팅방 및 상대 회원 정보, 대화 내역이 반환된다.")
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

            given(chatFacadeService.startChatroomByMemberId(any(Member.class), eq(TARGET_MEMBER_ID))).willReturn(response);

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

    }

}
