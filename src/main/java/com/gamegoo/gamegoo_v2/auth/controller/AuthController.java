package com.gamegoo.gamegoo_v2.auth.controller;

import com.gamegoo.gamegoo_v2.auth.jwt.JwtProvider;
import com.gamegoo.gamegoo_v2.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final JwtProvider jwtProvider;

    @GetMapping("/token/{memberId}")
    @Operation(summary = "임시 access token 발급 API", description = "테스트용으로 access token을 발급받을 수 있는 API 입니다.")
    public ApiResponse<String> getTestAccessToken(@PathVariable(name = "memberId") Long memberId) {
        return ApiResponse.ok(jwtProvider.createAccessToken(memberId));
    }

}
