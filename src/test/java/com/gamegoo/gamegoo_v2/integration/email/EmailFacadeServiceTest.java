package com.gamegoo.gamegoo_v2.integration.email;

import com.gamegoo.gamegoo_v2.email.domain.EmailVerifyRecord;
import com.gamegoo.gamegoo_v2.email.repository.EmailVerifyRecordRepository;
import com.gamegoo.gamegoo_v2.email.service.EmailFacadeService;
import com.gamegoo.gamegoo_v2.email.service.EmailService;
import com.gamegoo.gamegoo_v2.exception.EmailException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@SpringBootTest
@Transactional // DB 상태 롤백 보장
class EmailFacadeServiceIntegrationTest {

    @Autowired
    private EmailVerifyRecordRepository emailVerifyRecordRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmailFacadeService emailFacadeService;

    @Autowired
    private EmailService emailService;

    @DisplayName("이메일 인증 코드 전송 성공: 요청이 정상 처리되고 이메일이 전송된다.")
    @Test
    void sendEmailVerificationCode_ShouldSendSuccessfully() {
        // Given
        String email = "test@example.com";

        // 실제로 이메일이 전송되지 않도록 Mock
        doNothing().when(emailService).sendEmail(any(), any(), any(), any());

        // When // Then
        assertDoesNotThrow(() -> emailFacadeService.sendEmailVerificationCode(email));

        // 이메일 인증 레코드 저장 확인
        List<EmailVerifyRecord> records = emailVerifyRecordRepository.findTop3ByEmailOrderByUpdatedAtDesc(email);
        assertThat(records).hasSize(1); // 하나의 인증 레코드가 저장되어야 함
    }

    @DisplayName("이메일 인증 코드 전송 실패: 3분 이내에 3번 이상 요청을 보낸 경우 예외가 발생한다.")
    @Test
    void sendEmailVerificationCode_ShouldThrowWhenTooManyRequests() {
        // Given
        String email = "test@example.com";

        // 실제로 이메일이 전송되지 않도록 Mock
        doNothing().when(JavaMailSender)

        // 최근 3개의 요청을 3분 이내로 저장
        emailVerifyRecordRepository.save(new EmailVerifyRecord(email, "code1", LocalDateTime.now().minusMinutes(2)));
        emailVerifyRecordRepository.save(new EmailVerifyRecord(email, "code2", LocalDateTime.now().minusMinutes(2)));
        emailVerifyRecordRepository.save(new EmailVerifyRecord(email, "code3", LocalDateTime.now().minusMinutes(2)));

        // When // Then
        assertThatThrownBy(() -> emailFacadeService.sendEmailVerificationCode(email))
                .isInstanceOf(EmailException.class)
                .hasMessage(ErrorCode.EMAIL_LIMIT_EXCEEDED.getMessage());
    }

    @DisplayName("이메일 전송 실패: 메일 서버 장애로 인해 이메일 전송에 실패한다.")
    @Test
    void sendEmail_ShouldThrowWhenMailServerDown() {
        // Given
        String email = "test@example.com";

        // 실제로 이메일이 전송되지 않도록 Mock
        doNothing().when(emailService).sendEmail(any(), any(), any(), any());

        // When // Then
        assertThatThrownBy(() -> emailFacadeService.sendEmailVerificationCode(email))
                .isInstanceOf(EmailException.class)
                .hasMessage(ErrorCode.EMAIL_SEND_FAIL.getMessage());
    }

}
