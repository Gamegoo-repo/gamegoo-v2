package com.gamegoo.gamegoo_v2.repository.notification;

import com.gamegoo.gamegoo_v2.config.JpaAuditingConfig;
import com.gamegoo.gamegoo_v2.config.QuerydslConfig;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.notification.domain.Notification;
import com.gamegoo.gamegoo_v2.notification.domain.NotificationType;
import com.gamegoo.gamegoo_v2.notification.domain.NotificationTypeTitle;
import com.gamegoo.gamegoo_v2.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({QuerydslConfig.class, JpaAuditingConfig.class})
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager em;

    private static final int PAGE_SIZE = 10;

    private NotificationType testNotificationType;
    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember("test@gmail.com", "member");
        testNotificationType = em.persist(NotificationType.create(NotificationTypeTitle.TEST_ALARM));
    }

    @Nested
    @DisplayName("알림 전체 목록 조회")
    class findNotificationsByMemberTest {

        @DisplayName("알림이 없는 경우")
        @Test
        void findNotificationsByMemberWhenNoResult() {
            // given
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

            // when
            Page<Notification> notificationPage =
                    notificationRepository.findNotificationsByMember(member, pageRequest);

            // then
            assertThat(notificationPage).isEmpty();
            assertThat(notificationPage.getTotalElements()).isEqualTo(0);
            assertThat(notificationPage.getTotalPages()).isEqualTo(0);
            assertTrue(notificationPage.isFirst());
            assertTrue(notificationPage.isLast());
        }

        @DisplayName("알림이 10개 이하일 때 첫번째 페이지 요청")
        @Test
        void findNotificationByMemberFirstPage() {
            // given
            Notification testNotification1 = createTestNotification(member);
            createTestNotification(member);
            createTestNotification(member);
            createTestNotification(member);
            Notification testNotification5 = createTestNotification(member);

            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

            // when
            Page<Notification> notificationPage = notificationRepository.findNotificationsByMember(member, pageRequest);

            // then
            assertThat(notificationPage).isNotEmpty();
            assertThat(notificationPage.getContent()).hasSize(5);
            assertThat(notificationPage.getTotalElements()).isEqualTo(5);
            assertThat(notificationPage.getTotalPages()).isEqualTo(1);
            assertThat(notificationPage.isFirst()).isTrue();
            assertThat(notificationPage.isLast()).isTrue();

            // 목록 정렬 검증
            assertThat(notificationPage.getContent().get(0).getId()).isEqualTo(testNotification5.getId());
            assertThat(notificationPage.getContent().get(4).getId()).isEqualTo(testNotification1.getId());
        }

        @DisplayName("알림이 10개 초과일 때 두번째 페이지 요청")
        @Test
        void findNotificationByMemberSecondPage() {
            // given
            for (int i = 1; i <= 15; i++) {
                createTestNotification(member);
            }

            PageRequest pageRequest = PageRequest.of(1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

            // when
            Page<Notification> notificationPage = notificationRepository.findNotificationsByMember(member, pageRequest);

            // then
            assertThat(notificationPage).isNotEmpty();
            assertThat(notificationPage.getContent()).hasSize(5);
            assertThat(notificationPage.getTotalElements()).isEqualTo(15);
            assertThat(notificationPage.getTotalPages()).isEqualTo(2);
            assertThat(notificationPage.isFirst()).isFalse();
            assertThat(notificationPage.isLast()).isTrue();
        }

    }

    private Member createMember(String email, String gameName) {
        return em.persist(Member.builder()
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

    private Notification createTestNotification(Member member) {
        return em.persist(Notification.create(member, null, testNotificationType, testNotificationType.getContent()));
    }

}
