package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FriendRepositoryCustom {

    Slice<Friend> findFriendsByCursorAndOrdered(Long memberId, Long cursor, Integer pageSize);

    List<Friend> findFriendsByQueryStringAndOrdered(Long memberId, String queryString);

}
