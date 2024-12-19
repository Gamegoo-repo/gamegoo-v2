package com.gamegoo.gamegoo_v2.notification.repository;

import com.gamegoo.gamegoo_v2.notification.domain.Notification;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryCustom {

    Slice<Notification> findNotificationsByCursorAndOrdered(Long memberId, Long cursor, Integer pageSize);

}
