package com.gamegoo.gamegoo_v2.integration.email;

import com.gamegoo.gamegoo_v2.email.domain.EmailVerifyRecord;
import com.gamegoo.gamegoo_v2.email.repository.EmailVerifyRecordRepository;
import com.gamegoo.gamegoo_v2.email.service.EmailService;
import com.gamegoo.gamegoo_v2.exception.EmailException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    @Mock
    private EmailVerifyRecordRepository emailVerifyRecordRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("이메일 인증 코드 전송 성공: 요청이 정상 처리되고 이메일이 전송된다.")
    @Test
    void sendEmailVerificationCode_ShouldSendSuccessfully() {
        // Given
        String email = "test@example.com";
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);
        when(emailVerifyRecordRepository.findTop3ByEmailOrderByUpdatedAtDesc(email)).thenReturn(new ArrayList<>());

        // When // Then
        assertDoesNotThrow(() -> emailService.sendEmailVerificationCode(email));
        verify(emailVerifyRecordRepository, times(1)).save(any(EmailVerifyRecord.class));
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @DisplayName("이메일 인증 코드 전송 실패: 3분 이내에 3번 이상 요청을 보낸 경우 예외가 발생한다.")
    @Test
    void sendEmailVerificationCode_ShouldThrowWhenTooManyRequests() {
        // Given
        String email = "test@example.com";
        List<EmailVerifyRecord> recentRecords = new ArrayList<>();
        recentRecords.add(new EmailVerifyRecord(email, "code1", LocalDateTime.now().minusMinutes(2)));
        recentRecords.add(new EmailVerifyRecord(email, "code2", LocalDateTime.now().minusMinutes(2)));
        recentRecords.add(new EmailVerifyRecord(email, "code3", LocalDateTime.now().minusMinutes(2)));
        when(emailVerifyRecordRepository.findTop3ByEmailOrderByUpdatedAtDesc(email)).thenReturn(recentRecords);

        // When // Then
        assertThatThrownBy(() -> emailService.sendEmailVerificationCode(email))
                .isInstanceOf(EmailException.class)
                .hasMessage(ErrorCode.EMAIL_LIMIT_EXCEEDED.getMessage());
        verify(emailVerifyRecordRepository, never()).save(any(EmailVerifyRecord.class));
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @DisplayName("이메일 전송 실패: 메일 서버 장애로 인해 이메일 전송에 실패한다.")
    @Test
    void sendEmail_ShouldThrowWhenMailServerDown() {
        // Given
        String email = "test@example.com";
        String templatePath = "templates/email-template.html";
        String subject = "Test Email";
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("CERTIFICATION_NUMBER", "123456");

        doThrow(new RuntimeException("Mail server down")).when(javaMailSender).send(any(MimeMessage.class));

        // When // Then
        assertThatThrownBy(() -> emailService.sendEmail(email, subject, templatePath, placeholders))
                .isInstanceOf(EmailException.class)
                .hasMessage(ErrorCode.EMAIL_SEND_FAIL.getMessage());
    }

}
