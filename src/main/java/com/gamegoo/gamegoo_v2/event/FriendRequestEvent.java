package com.gamegoo.gamegoo_v2.event;

import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestEvent {

    private final Member member;
    private final Member sourceMember;

}
