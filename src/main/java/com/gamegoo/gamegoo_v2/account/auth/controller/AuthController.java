package com.gamegoo.gamegoo_v2.account.auth.controller;

import com.gamegoo.gamegoo_v2.account.auth.dto.request.JoinRequest;
import com.gamegoo.gamegoo_v2.account.auth.dto.request.LoginRequest;
import com.gamegoo.gamegoo_v2.account.auth.dto.response.LoginResponse;
import com.gamegoo.gamegoo_v2.account.auth.jwt.JwtProvider;
import com.gamegoo.gamegoo_v2.account.auth.service.AuthFacadeService;
import com.gamegoo.gamegoo_v2.core.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final AuthFacadeService authFacadeService;

    @GetMapping("/token/{memberId}")
    @Operation(summary = "임시 access token 발급 API", description = "테스트용으로 access token을 발급받을 수 있는 API 입니다.")
    public ApiResponse<String> getTestAccessToken(@PathVariable(name = "memberId") Long memberId) {
        return ApiResponse.ok(jwtProvider.createAccessToken(memberId));
    }

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "회원가입 API입니다.")
    public ApiResponse<String> join(@Valid @RequestBody JoinRequest request) {
        authFacadeService.join(request);
        return ApiResponse.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 API입니다.")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authFacadeService.login(request));
    }

}
