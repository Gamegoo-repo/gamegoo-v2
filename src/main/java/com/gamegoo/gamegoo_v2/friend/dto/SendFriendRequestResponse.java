package com.gamegoo.gamegoo_v2.friend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendFriendRequestResponse {

    Long targetMemberId;
    String message;

    public static SendFriendRequestResponse of(Long targetMemberId, String message) {
        return SendFriendRequestResponse.builder()
                .targetMemberId(targetMemberId)
                .message(message)
                .build();
    }

}
