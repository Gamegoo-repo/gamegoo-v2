package com.gamegoo.gamegoo_v2.controller.friend;

import com.gamegoo.gamegoo_v2.controller.ControllerTestSupport;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.controller.FriendController;
import com.gamegoo.gamegoo_v2.friend.dto.FriendRequestResponse;
import com.gamegoo.gamegoo_v2.friend.service.FriendFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
class FriendControllerTest extends ControllerTestSupport {

    @MockitoBean
    FriendFacadeService friendFacadeService;

    private static final String API_URL_PREFIX = "/api/v2/friends";
    private static final Long TARGET_MEMBER_ID = 2L;

    @Nested
    @DisplayName("친구 요청 전송")
    class SendFriendRequestTest {

        @DisplayName("친구 요청 전송 성공")
        @Test
        void sendFriendRequestSucceeds() throws Exception {
            // given
            FriendRequestResponse response = FriendRequestResponse.builder()
                    .targetMemberId(TARGET_MEMBER_ID)
                    .message("친구 요청 전송 성공")
                    .build();

            given(friendFacadeService.sendFriendRequest(any(), any())).willReturn(response);

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.message").value("친구 요청 전송 성공"))
                    .andExpect(jsonPath("$.data.targetMemberId").value(TARGET_MEMBER_ID));
        }

        @DisplayName("친구 요청 전송 실패: 본인 id를 요청한 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenTargetIsSelf() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.FRIEND_BAD_REQUEST))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.FRIEND_BAD_REQUEST.getMessage()));
        }

        @DisplayName("친구 요청 전송 실패: 친구 요청 상대가 탈퇴한 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenTargetIsBlind() throws Exception {
            // given
            willThrow(new MemberException(ErrorCode.TARGET_MEMBER_DEACTIVATED))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(ErrorCode.TARGET_MEMBER_DEACTIVATED.getMessage()));
        }

        @DisplayName("친구 요청 전송 실패: 내가 상대를 차단한 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenTargetIsBlocked() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.FRIEND_TARGET_IS_BLOCKED))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.FRIEND_TARGET_IS_BLOCKED.getMessage()));
        }

        @DisplayName("친구 요청 전송 실패: 상대가 나를 차단한 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenBlockedByTarget() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.BLOCKED_BY_FRIEND_TARGET))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.BLOCKED_BY_FRIEND_TARGET.getMessage()));
        }

        @DisplayName("친구 요청 전송 실패: 두 회원이 이미 친구 관계인 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenAlreadyFriend() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.ALREADY_FRIEND))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_FRIEND.getMessage()));
        }

        @DisplayName("친구 요청 전송 실패: 내가 보낸 수락 대기 중인 친구 요청이 이미 존재하는 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenPendingRequestToTargetExists() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.MY_PENDING_FRIEND_REQUEST_EXIST))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.MY_PENDING_FRIEND_REQUEST_EXIST.getMessage()));
        }

        @DisplayName("친구 요청 전송 실패: 상대가 보낸 수락 대기 중인 친구 요청이 이미 존재하는 경우 에러 응답을 반환한다.")
        @Test
        void sendFriendRequestFailedWhenPendingRequestToMeExists() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.TARGET_PENDING_FRIEND_REQUEST_EXIST))
                    .given(friendFacadeService).sendFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(post(API_URL_PREFIX + "/request/{memberId}", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.TARGET_PENDING_FRIEND_REQUEST_EXIST.getMessage()));
        }

    }

    @Nested
    @DisplayName("친구 요청 수락")
    class AcceptFriendRequestTest {

        @DisplayName("친구 요청 수락 성공")
        @Test
        void acceptFriendRequestSucceeds() throws Exception {
            // given
            FriendRequestResponse response = FriendRequestResponse.builder()
                    .targetMemberId(TARGET_MEMBER_ID)
                    .message("친구 요청 수락 성공")
                    .build();

            given(friendFacadeService.acceptFriendRequest(any(), any())).willReturn(response);

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/request/{memberId}/accept", TARGET_MEMBER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.message").value("친구 요청 수락 성공"))
                    .andExpect(jsonPath("$.data.targetMemberId").value(TARGET_MEMBER_ID));
        }

        @DisplayName("친구 요청 수락 실패: 본인 id를 요청한 경우 에러 응답을 반환한다.")
        @Test
        void acceptFriendRequestFailedWhenTargetIsSelf() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.FRIEND_BAD_REQUEST))
                    .given(friendFacadeService).acceptFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/request/{memberId}/accept", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.FRIEND_BAD_REQUEST.getMessage()));
        }

        @DisplayName("친구 요청 수락 실패: PENDING 상태인 친구 요청이 없는 경우 에러 응답을 반환한다.")
        @Test
        void acceptFriendRequestFailedWhenNoPendingRequest() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST))
                    .given(friendFacadeService).acceptFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/request/{memberId}/accept", TARGET_MEMBER_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST.getMessage()));
        }

    }

    @Nested
    @DisplayName("친구 요청 거절")
    class rejectFriendRequestTest {

        @DisplayName("친구 요청 거절 성공")
        @Test
        void rejectFriendRequestSucceeds() throws Exception {
            // given
            FriendRequestResponse response = FriendRequestResponse.builder()
                    .targetMemberId(TARGET_MEMBER_ID)
                    .message("친구 요청 거절 성공")
                    .build();

            given(friendFacadeService.rejectFriendRequest(any(), any())).willReturn(response);

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/request/{memberId}/reject", TARGET_MEMBER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.message").value("친구 요청 거절 성공"))
                    .andExpect(jsonPath("$.data.targetMemberId").value(TARGET_MEMBER_ID));
        }

        @DisplayName("친구 요청 거절 실패: 본인 id를 요청한 경우 에러 응답을 반환한다.")
        @Test
        void rejectFriendRequestFailedWhenTargetIsSelf() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.FRIEND_BAD_REQUEST))
                    .given(friendFacadeService).rejectFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/request/{memberId}/reject", TARGET_MEMBER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.FRIEND_BAD_REQUEST.getMessage()));
        }

        @DisplayName("친구 요청 거절 실패: PENDING 상태인 친구 요청이 없는 경우 에러 응답을 반환한다.")
        @Test
        void rejectFriendRequestFailedWhenNoPendingRequest() throws Exception {
            // given
            willThrow(new FriendException(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST))
                    .given(friendFacadeService).rejectFriendRequest(any(), eq(TARGET_MEMBER_ID));

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/request/{memberId}/reject", TARGET_MEMBER_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST.getMessage()));
        }

    }

}
