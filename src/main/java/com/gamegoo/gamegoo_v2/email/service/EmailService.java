package com.gamegoo.gamegoo_v2.email.service;

import com.gamegoo.gamegoo_v2.email.repository.EmailVerifyRecordRepository;
import com.gamegoo.gamegoo_v2.utils.RandomCodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    private final EmailVerifyRecordRepository emailVerifyRecordRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;

    public void sendEmailVerificationCode(String email){
        // 랜덤 코드 생성하기
        String certificationNumber = RandomCodeGeneratorUtil.generateEmailRandomCode();

    }


}
