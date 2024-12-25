package com.gamegoo.gamegoo_v2.account.auth.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.repository.MemberRepository;
import com.gamegoo.gamegoo_v2.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasswordService {

    private final MemberRepository memberRepository;

    /**
     * 비밀번호 변경
     *
     * @param member
     * @param newPassword
     */
    @Transactional
    public void changePassword(Member member, String newPassword) {
        String encodedPassword = PasswordUtil.encodePassword(newPassword);
        member.updatePassword(encodedPassword);
        memberRepository.save(member);
    }

}
