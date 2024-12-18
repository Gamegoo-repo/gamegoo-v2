package com.gamegoo.gamegoo_v2.email.controller;

import com.gamegoo.gamegoo_v2.common.ApiResponse;
import com.gamegoo.gamegoo_v2.email.dto.EmailRequest;
import com.gamegoo.gamegoo_v2.email.service.EmailFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email", description = "Email 전송 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/email")
@Validated
public class EmailController {

    private final EmailFacadeService emailFacadeService;


    @PostMapping("/send/join")
    @Operation(summary = "회원가입용 이메일 인증코드 전송 API 입니다. 중복확인 포함", description = "API for sending email for join")
    public ApiResponse<String> sendEmailwithCheckDuplication(
            @Valid @RequestBody EmailRequest request) {
        emailFacadeService.sendEmailVerificationCode(request);
        return ApiResponse.ok("인증 이메일을 발송했습니다.");
    }

}
