package com.gamegoo.gamegoo_v2.chat.repository;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;

import java.util.List;
import java.util.Map;

public interface MemberChatroomRepositoryCustom {

    Map<Long, Member> findTargetMembersBatch(List<Long> chatroomIds, Long memberId);

}
