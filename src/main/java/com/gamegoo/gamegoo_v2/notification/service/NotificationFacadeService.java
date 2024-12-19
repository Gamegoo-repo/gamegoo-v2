package com.gamegoo.gamegoo_v2.notification.service;

import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.notification.domain.Notification;
import com.gamegoo.gamegoo_v2.notification.dto.NotificationCursorListResponse;
import com.gamegoo.gamegoo_v2.notification.dto.NotificationPageListResponse;
import com.gamegoo.gamegoo_v2.notification.dto.ReadNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
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

    /**
     * 알림 전체 목록 조회 Facade 메소드
     *
     * @param member
     * @param pageIdx
     * @return
     */
    public NotificationPageListResponse getNotificationPageList(Member member, Integer pageIdx) {
        Page<Notification> notificationPage = notificationService.getNotificationPage(member, pageIdx);

        return NotificationPageListResponse.of(notificationPage);
    }

    /**
     * 알림 팝업 목록 조회 Facade 메소드
     *
     * @param member
     * @param cursorId
     * @return
     */
    public NotificationCursorListResponse getNotificationCursorList(Member member, Long cursorId) {
        Slice<Notification> notificationSlice = notificationService.getNotificationSlice(member, cursorId);

        return NotificationCursorListResponse.of(notificationSlice);
    }

}
