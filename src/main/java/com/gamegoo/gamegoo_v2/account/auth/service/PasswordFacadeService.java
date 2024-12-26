package com.gamegoo.gamegoo_v2.account.auth.service;

import com.gamegoo.gamegoo_v2.account.auth.dto.PasswordRequest;
import com.gamegoo.gamegoo_v2.account.email.service.EmailService;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasswordFacadeService {

    private final PasswordService passwordService;
    private final EmailService emailService;
    private final MemberService memberService;

    @Transactional
    public String changePassword(PasswordRequest request) {
        // 이메일, 코드 검증
        emailService.verifyEmailCode(request.getEmail(), request.getVerifyCode());
        Member member = memberService.findMemberByEmail(request.getEmail());

        // 새로운 비밀번호 설정
        passwordService.changePassword(member, request.getNewPassword());
        return "비밀번호 변경이 완료되었습니다.";
    }

}
