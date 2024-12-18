package com.gamegoo.gamegoo_v2.controller.notification;

import com.gamegoo.gamegoo_v2.controller.ControllerTestSupport;
import com.gamegoo.gamegoo_v2.exception.NotificationException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.notification.controller.NotificationController;
import com.gamegoo.gamegoo_v2.notification.dto.ReadNotificationResponse;
import com.gamegoo.gamegoo_v2.notification.service.NotificationFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest extends ControllerTestSupport {

    @MockitoBean
    NotificationFacadeService notificationFacadeService;

    private static final String API_URL_PREFIX = "/api/v2/notification";
    private static final Long NOTIFICATION_ID = 1L;

    @Nested
    @DisplayName("알림 읽음 처리")
    class ReadNotificationTest {

        @DisplayName("알림 읽음 처리 성공")
        @Test
        void readNotificationSucceeds() throws Exception {
            // given
            ReadNotificationResponse response = ReadNotificationResponse.of(NOTIFICATION_ID);

            given(notificationFacadeService.readNotification(any(Member.class), any(Long.class))).willReturn(response);

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/{notificationId}", NOTIFICATION_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.message").value("알림 읽음 처리 성공"))
                    .andExpect(jsonPath("$.data.notificationId").value(NOTIFICATION_ID));

        }

        @DisplayName("알림 읽음 처리 실패: id에 해당하는 알림이 없는 경우 에러 응답을 반환한다.")
        @Test
        void readNotificationFailedWhenNotificationNotExists() throws Exception {
            // given
            willThrow(new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND))
                    .given(notificationFacadeService).readNotification(any(Member.class), any(Long.class));

            // when // then
            mockMvc.perform(patch(API_URL_PREFIX + "/{notificationId}", NOTIFICATION_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage()));
        }

    }


}