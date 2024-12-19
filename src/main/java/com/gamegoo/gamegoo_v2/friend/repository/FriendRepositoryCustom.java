package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FriendRepositoryCustom {

    Slice<Friend> findFriendsByCursor(Long memberId, Long cursor, int pageSize);

    List<Friend> findFriendsByQueryString(Long memberId, String queryString);

    boolean isFriend(Long memberId, Long targetMemberId);

}
