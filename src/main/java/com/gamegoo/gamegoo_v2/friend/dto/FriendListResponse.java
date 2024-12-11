package com.gamegoo.gamegoo_v2.friend.dto;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
public class FriendListResponse {

    List<FriendInfoResponse> friendInfoDTOList;
    int list_size;
    boolean has_next;
    Long next_cursor;

    @Getter
    @Builder
    public static class FriendInfoResponse {

        Long memberId;
        String name;
        int profileImg;
        boolean isLiked;
        boolean isBlind;

        public static FriendInfoResponse of(Friend friend) {
            String name = friend.getToMember().isBlind() ? "(탈퇴한 사용자)" : friend.getToMember().getGameName();
            return FriendInfoResponse.builder()
                    .memberId(friend.getToMember().getId())
                    .profileImg(friend.getToMember().getProfileImage())
                    .name(name)
                    .isLiked(friend.isLiked())
                    .isBlind(friend.getToMember().isBlind())
                    .build();
        }

    }

    public static FriendListResponse of(Slice<Friend> friends) {
        List<FriendInfoResponse> friendInfoResponseList = friends.stream()
                .map(FriendInfoResponse::of)
                .toList();

        Long nextCursor = friends.hasNext()
                ? friends.getContent().get(friendInfoResponseList.size() - 1).getToMember().getId()
                : null;

        return FriendListResponse.builder()
                .friendInfoDTOList(friendInfoResponseList)
                .list_size(friendInfoResponseList.size())
                .has_next(friends.hasNext())
                .next_cursor(nextCursor)
                .build();
    }

}
