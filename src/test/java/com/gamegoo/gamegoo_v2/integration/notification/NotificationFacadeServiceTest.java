package com.gamegoo.gamegoo_v2.integration.notification;

import com.gamegoo.gamegoo_v2.exception.NotificationException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import com.gamegoo.gamegoo_v2.notification.domain.Notification;
import com.gamegoo.gamegoo_v2.notification.domain.NotificationType;
import com.gamegoo.gamegoo_v2.notification.domain.NotificationTypeTitle;
import com.gamegoo.gamegoo_v2.notification.dto.ReadNotificationResponse;
import com.gamegoo.gamegoo_v2.notification.repository.NotificationRepository;
import com.gamegoo.gamegoo_v2.notification.repository.NotificationTypeRepository;
import com.gamegoo.gamegoo_v2.notification.service.NotificationFacadeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class NotificationFacadeServiceTest {

    @Autowired
    NotificationFacadeService notificationFacadeService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationTypeRepository notificationTypeRepository;

    private static final String MEMBER_EMAIL = "test@gmail.com";
    private static final String MEMBER_GAMENAME = "member";

    private Member member;
    private NotificationType mannerLevelUpNotificationType;

    @BeforeEach
    void setUp() {
        member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        initNotificationType();
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAllInBatch();
        notificationTypeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("알림 읽음 처리")
    class ReadNotificationTest {

        @DisplayName("알림 읽음 처리 성공")
        @Test
        void readNotificationSucceeds() {
            // given
            Notification notification = createMannerLevelUpNotification(member);

            // when
            ReadNotificationResponse response = notificationFacadeService.readNotification(member,
                    notification.getId());

            // then
            assertThat(response.getNotificationId()).isEqualTo(notification.getId());
            assertThat(response.getMessage()).isEqualTo("알림 읽음 처리 성공");

            Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
            assertThat(updatedNotification.isRead()).isEqualTo(true);
        }

        @DisplayName("알림 읽음 처리 실패: id에 해당하는 알림이 없는 경우 예외가 발생한다.")
        @Test
        void readNotification_shouldThrowWhenNotificationNotExists() {
            // when // then
            assertThatThrownBy(() -> notificationFacadeService.readNotification(member, 1L))
                    .isInstanceOf(NotificationException.class)
                    .hasMessage(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
        }

    }

    @DisplayName("안읽은 알림 개수 조회 성공")
    @Test
    void countUnreadNotificationSucceeds() {
        // given
        // 알림 생성
        Notification notification1 = createMannerLevelUpNotification(member);
        Notification notification2 = createMannerLevelUpNotification(member);
        Notification notification3 = createMannerLevelUpNotification(member);
        Notification notification4 = createMannerLevelUpNotification(member);
        Notification notification5 = createMannerLevelUpNotification(member);

        // 알림 2개 읽음 처리
        notification1.updateIsRead(true);
        notification2.updateIsRead(true);
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        // when
        Integer count = notificationFacadeService.countUnreadNotification(member);

        // then
        assertThat(count).isEqualTo(3);
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

    private void initNotificationType() {
        notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.FRIEND_REQUEST_SEND));
        notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.FRIEND_REQUEST_RECEIVED));
        notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.FRIEND_REQUEST_ACCEPTED));
        notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.FRIEND_REQUEST_REJECTED));
        mannerLevelUpNotificationType =
                notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.MANNER_LEVEL_UP));
        notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.MANNER_LEVEL_DOWN));
        notificationTypeRepository.save(NotificationType.create(NotificationTypeTitle.MANNER_KEYWORD_RATED));
    }

    /**
     * 매너 레벨 상승 알림 생성
     *
     * @param member
     * @return
     */
    private Notification createMannerLevelUpNotification(Member member) {
        String notificationContent = mannerLevelUpNotificationType.getContent().replace("n", Integer.toString(2));
        Notification notification = Notification.create(member, null, mannerLevelUpNotificationType,
                notificationContent);
        return notificationRepository.save(notification);
    }

}
