package com.gamegoo.gamegoo_v2.email.service;

import com.gamegoo.gamegoo_v2.email.dto.EmailCodeRequest;
import com.gamegoo.gamegoo_v2.email.dto.EmailRequest;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailFacadeService {

    private final EmailService emailService;
    private final MemberService memberService;

    @Transactional
    public void verifyEmailCode(EmailCodeRequest request) {
        emailService.verifyEmailCode(request.getEmail(), request.getCode());
    }

    @Transactional
    public void sendVerificationCodeWithDuplicationCheck(EmailRequest request) {
        memberService.checkExistMemberByEmail(request.getEmail());
        emailService.sendEmailVerificationCode(request.getEmail());
    }

    @Transactional
    public void sendEmailVerificationCode(Member member) {
        emailService.sendEmailVerificationCode(member.getEmail());
    }

}
