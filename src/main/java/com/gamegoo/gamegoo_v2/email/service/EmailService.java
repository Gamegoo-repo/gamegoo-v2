package com.gamegoo.gamegoo_v2.email.service;

import com.gamegoo.gamegoo_v2.email.domain.EmailVerifyRecord;
import com.gamegoo.gamegoo_v2.email.repository.EmailVerifyRecordRepository;
import com.gamegoo.gamegoo_v2.exception.EmailException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.utils.RandomCodeGeneratorUtil;
import com.gamegoo.gamegoo_v2.utils.EmailTemplateProcessor;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailService {
    private final EmailVerifyRecordRepository emailVerifyRecordRepository;
    private final JavaMailSender javaMailSender;

    /**
     * 랜덤 코드 이메일 전송
     * @param email 이메일 주소
     */
    public void sendEmailVerificationCode(String email){
        // 해당 이메일로 3분 이내에 3번 이상 요청을 보냈을 경우 제한
        checkEmailSendRequestLimit(email);

        // 랜덤 코드 생성하기
        String certificationNumber = RandomCodeGeneratorUtil.generateEmailRandomCode();

        // HTML 템플릿 경로
        String templatePath = "templates/email-template.html";

        // Placeholder 값 정의
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("CERTIFICATION_NUMBER", certificationNumber);

        // 이메일 전송
        sendEmail(email, "GameGoo 이메일 인증 코드", templatePath, placeholders);

        // 이메일 전송 기록 DB 저장
        EmailVerifyRecord emailVerifyRecord = EmailVerifyRecord.create(email, certificationNumber);
        emailVerifyRecordRepository.save(emailVerifyRecord);
    }

    /**
     * 이메일 전송 메소드
     * @param email        수신자 이메일
     * @param subject      이메일 제목
     * @param templatePath 템플릿 파일 경로
     * @param placeholders 템플릿에 삽입할 Placeholder 값
     */
    public void sendEmail(String email, String subject, String templatePath, Map<String, String> placeholders) {
        try {
            // 템플릿 처리
            String htmlContent = EmailTemplateProcessor.processTemplate(templatePath, placeholders);

            // MimeMessage 생성
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);

            // 이메일 전송
            javaMailSender.send(message);

        } catch (Exception e) {
            log.error("이메일 전송에 실패했습니다.");
            throw new EmailException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    /**
     * 특정 이메일로 3분 이내 3개 미만의 이메일을 보냈는지 검증
     * @param email 이메일 주소
     */
    protected void checkEmailSendRequestLimit(String email) {
        // 최근 3개의 기록 가져오기
        List<EmailVerifyRecord> recentRecords = emailVerifyRecordRepository.findTop3ByEmailOrderByUpdatedAtDesc(email);

        // 3개의 기록이 존재할 경우만 처리
        if (recentRecords.size() == 3) {
            LocalDateTime now = LocalDateTime.now();

            // 모든 기록이 3분 이내인지 확인
            boolean allWithin3Minutes = recentRecords.stream()
                    .allMatch(record -> Duration.between(record.getUpdatedAt(), now).toMinutes() < 3);

            if (allWithin3Minutes) {
                log.warn("3분 이내에 3개 이상의 이메일을 이미 보냈습니다. {}", email);
                throw new EmailException(ErrorCode.EMAIL_LIMIT_EXCEEDED);
            }
        }
    }
}
