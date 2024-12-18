package com.gamegoo.gamegoo_v2.member.service;

import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public void create(String email, String password, boolean isAgree) {

    }

    /**
     * 회원 정보 조회
     *
     * @param memberId 사용자 ID
     * @return Member
     */
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * Email 중복 확인
     *
     * @param email email
     */
    public void checkExistMemberByEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

}
