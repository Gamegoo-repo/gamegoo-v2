package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import org.springframework.data.domain.Slice;

public interface FriendRepositoryCustom {

    Slice<Friend> findFriendsByCursorAndOrdered(Long memberId, Long cursor, Integer pageSize);

}
