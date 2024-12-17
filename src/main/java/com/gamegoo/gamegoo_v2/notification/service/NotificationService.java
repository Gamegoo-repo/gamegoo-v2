package com.gamegoo.gamegoo_v2.notification.service;

import com.gamegoo.gamegoo_v2.exception.NotificationException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.manner.domain.MannerKeyword;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.notification.domain.Notification;
import com.gamegoo.gamegoo_v2.notification.domain.NotificationType;
import com.gamegoo.gamegoo_v2.notification.domain.NotificationTypeTitle;
import com.gamegoo.gamegoo_v2.notification.repository.NotificationRepository;
import com.gamegoo.gamegoo_v2.notification.repository.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationRepository notificationRepository;

    private static final String PLACEHOLDER = "n";


    /**
     * 친구 요청 전송됨 알림 생성 메소드
     *
     * @param member
     * @param sourceMember
     * @return
     */
    public Notification createSendFriendRequestNotification(Member member, Member sourceMember) {
        validateMember(member);
        validateMember(sourceMember);
        NotificationType notificationType = findNotificationType(NotificationTypeTitle.FRIEND_REQUEST_SEND);
        return saveNotification(notificationType, notificationType.getContent(), member, sourceMember);
    }

    /**
     * 친구 요청 받음 알림 생성 메소드
     *
     * @param member
     * @param sourceMember
     * @return
     */
    public Notification createReceivedFriendRequestNotification(Member member, Member sourceMember) {
        validateMember(member);
        validateMember(sourceMember);

        NotificationType notificationType = findNotificationType(NotificationTypeTitle.FRIEND_REQUEST_RECEIVED);
        return saveNotification(notificationType, notificationType.getContent(), member, sourceMember);
    }

    /**
     * 친구 요청 수락됨 알림 생성 메소드
     *
     * @param member
     * @param sourceMember
     * @return
     */
    public Notification createAcceptFriendRequestNotification(Member member, Member sourceMember) {
        validateMember(member);
        validateMember(sourceMember);

        NotificationType notificationType = findNotificationType(NotificationTypeTitle.FRIEND_REQUEST_ACCEPTED);
        return saveNotification(notificationType, notificationType.getContent(), member, sourceMember);
    }

    /**
     * 친구 요청 거절됨 알림 생성 메소드
     *
     * @param member
     * @param sourceMember
     * @return
     */
    public Notification createRejectFriendRequestNotification(Member member, Member sourceMember) {
        validateMember(member);
        validateMember(sourceMember);

        NotificationType notificationType = findNotificationType(NotificationTypeTitle.FRIEND_REQUEST_REJECTED);
        return saveNotification(notificationType, notificationType.getContent(), member, sourceMember);
    }

    /**
     * 매너레벨 상승/하락 알림 생성 메소드
     *
     * @param notificationTypeTitle
     * @param member
     * @param mannerLevel
     * @return
     */
    public Notification createMannerLevelNotification(NotificationTypeTitle notificationTypeTitle, Member member,
            int mannerLevel) {
        validateMember(member);

        NotificationType notificationType = findNotificationType(notificationTypeTitle);
        String notificationContent = notificationType.getContent().replace(PLACEHOLDER, Integer.toString(mannerLevel));
        return saveNotification(notificationType, notificationContent, member, null);
    }

    /**
     * 매너 평가 등록됨 알림 생성 메소드
     *
     * @param mannerKeywordList
     * @param member
     * @return
     */
    public Notification createMannerRatingNotification(List<MannerKeyword> mannerKeywordList, Member member) {
        validateMember(member);

        NotificationType notificationType = findNotificationType(NotificationTypeTitle.MANNER_KEYWORD_RATED);

        String mannerKeywordString = mannerKeywordList.get(0).getContents();
        if (mannerKeywordList.size() > 1) {
            mannerKeywordString += " 외 " + (mannerKeywordList.size() - 1) + "개의";
        }

        String notificationContent = notificationType.getContent().replace(PLACEHOLDER, mannerKeywordString);

        return saveNotification(notificationType, notificationContent, member, null);
    }

    /**
     * 알림 생성 및 저장 메소드
     *
     * @param type
     * @param content
     * @param member
     * @param sourceMember
     * @return
     */
    private Notification saveNotification(NotificationType type, String content, Member member, Member sourceMember) {
        return notificationRepository.save(Notification.create(member, sourceMember, type, content));
    }

    /**
     * title로 NotificationType을 찾는 메소드
     *
     * @param title
     * @return
     */
    private NotificationType findNotificationType(NotificationTypeTitle title) {
        return notificationTypeRepository.findNotificationTypeByTitle(title)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND));
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new NotificationException(ErrorCode.NOTIFICATION_METHOD_BAD_REQUEST);
        }
    }

}
