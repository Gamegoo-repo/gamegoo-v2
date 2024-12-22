package com.gamegoo.gamegoo_v2.friend.dto;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
public class FriendListResponse {

    List<FriendInfoResponse> friendInfoList;
    int listSize;
    boolean hasNext;
    Long nextCursor;

    public static FriendListResponse of(Slice<Friend> friends) {
        List<FriendInfoResponse> friendInfoResponseList = friends.stream()
                .map(FriendInfoResponse::of)
                .toList();

        Long nextCursor = friends.hasNext()
                ? friends.getContent().get(friendInfoResponseList.size() - 1).getToMember().getId()
                : null;

        return FriendListResponse.builder()
                .friendInfoList(friendInfoResponseList)
                .listSize(friendInfoResponseList.size())
                .hasNext(friends.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

}
