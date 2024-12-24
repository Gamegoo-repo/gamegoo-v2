package com.gamegoo.gamegoo_v2.account.member.controller;

import com.gamegoo.gamegoo_v2.account.auth.annotation.AuthMember;
import com.gamegoo.gamegoo_v2.core.common.ApiResponse;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.dto.response.MyProfileResponse;
import com.gamegoo.gamegoo_v2.account.member.service.MemberFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/profile")
@Validated
public class MemberController {

    private final MemberFacadeService memberFacadeService;

    @Operation(summary = "내 프로필 조회 API 입니다. (jwt 토큰 O)", description = "API for looking up member with jwt")
    @GetMapping
    public ApiResponse<MyProfileResponse> getMemberJWT(@AuthMember Member member) {
        return ApiResponse.ok(memberFacadeService.getMyProfile(member));
    }

}
