package com.gamegoo.gamegoo_v2.social.friend.repository;

import com.gamegoo.gamegoo_v2.social.friend.domain.Friend;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface FriendRepositoryCustom {

    Slice<Friend> findFriendsByCursor(Long memberId, Long cursor, int pageSize);

    List<Friend> findFriendsByQueryString(Long memberId, String queryString);

    boolean isFriend(Long memberId, Long targetMemberId);

    Map<Long, Boolean> isFriendBatch(Long memberId, List<Long> targetMemberIds);

}
