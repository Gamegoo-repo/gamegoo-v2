package com.gamegoo.gamegoo_v2.email.service;

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
    public void sendEmailVerificationCode(String email) {
        memberService.checkExistMemberByEmail(email);
        emailService.sendEmailVerificationCode(email);
    }

}
