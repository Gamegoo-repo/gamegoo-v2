package com.gamegoo.gamegoo_v2.block.service;

import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockFacadeService {

    private final MemberService memberService;
    private final BlockService blockService;

    /**
     * 회원 차단 Facade 메소드
     *
     * @param member
     * @param targetMemberId
     */
    @Transactional
    public void blockMember(Member member, Long targetMemberId) {
        Member targetMember = memberService.findMember(targetMemberId);
        blockService.blockMember(member, targetMember);
    }

}
