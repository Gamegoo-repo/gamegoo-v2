package com.gamegoo.gamegoo_v2.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SendFriendRequestEvent {

    private final Long memberId;
    private final Long sourceMemberId;

}
