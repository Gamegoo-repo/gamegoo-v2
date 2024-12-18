package com.gamegoo.gamegoo_v2.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailFacadeService {

    private final EmailService emailService;

    @Transactional
    public void sendEmailVerificationCode(String email) {
        emailService.sendEmailVerificationCode(email);
    }

}
