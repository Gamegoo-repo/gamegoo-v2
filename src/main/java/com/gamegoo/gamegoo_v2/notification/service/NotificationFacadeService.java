package com.gamegoo.gamegoo_v2.notification.service;

import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.notification.domain.Notification;
import com.gamegoo.gamegoo_v2.notification.dto.ReadNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationFacadeService {

    private final NotificationService notificationService;

    /**
     * 알림 읽음 처리 Facade 메소드
     *
     * @param member
     * @param notificationId
     * @return
     */
    @Transactional
    public ReadNotificationResponse readNotification(Member member, Long notificationId) {
        Notification notification = notificationService.readNotification(member, notificationId);

        return ReadNotificationResponse.of(notification.getId());
    }

    /**
     * 안읽은 알림 개수 조회 Facade 메소드
     *
     * @param member
     * @return
     */
    public Integer countUnreadNotification(Member member) {
        return notificationService.countUnreadNotification(member);
    }

}
