package com.gamegoo.gamegoo_v2.notification.controller;

import com.gamegoo.gamegoo_v2.auth.annotation.AuthMember;
import com.gamegoo.gamegoo_v2.common.ApiResponse;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.notification.dto.ReadNotificationResponse;
import com.gamegoo.gamegoo_v2.notification.service.NotificationFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "Notification 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/notification")
public class NotificationController {

    private final NotificationFacadeService notificationFacadeService;

    @Operation(summary = "알림 읽음 처리 API", description = "특정 알림을 읽음 처리하는 API 입니다.")
    @Parameter(name = "notificationId", description = "읽음 처리할 알림의 id 입니다.")
    @PatchMapping("/{notificationId}")
    public ApiResponse<ReadNotificationResponse> getTotalNotificationList(
            @PathVariable(name = "notificationId") Long notificationId, @AuthMember Member member) {
        return ApiResponse.ok(notificationFacadeService.readNotification(member, notificationId));
    }

}
