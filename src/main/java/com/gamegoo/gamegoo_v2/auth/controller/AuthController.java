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
    @Operation(summary = "jwt 토큰 재발급 API", description = "access token, refresh token을 재발급 받는 API 입니다.")
    public ApiResponse<String> getTestAccessToken(@PathVariable(name = "memberId") Long memberId) {
        return ApiResponse.ok(jwtProvider.createAccessToken(memberId));
    }

}
