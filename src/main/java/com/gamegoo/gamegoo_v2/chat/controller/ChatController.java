package com.gamegoo.gamegoo_v2.chat.controller;

import com.gamegoo.gamegoo_v2.auth.annotation.AuthMember;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.chat.service.ChatFacadeService;
import com.gamegoo.gamegoo_v2.common.ApiResponse;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chat", description = "Chat 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class ChatController {

    private final ChatFacadeService chatFacadeService;

    @Operation(summary = "특정 회원과 채팅방 시작 API",
            description = "특정 대상 회원과의 채팅방을 시작하는 API 입니다.\n\n" +
                    "대상 회원과의 채팅방이 이미 존재하는 경우, 채팅방 uuid, 상대 회원 정보와 채팅 메시지 내역 등을 반환합니다.\n\n" +
                    "대상 회원과의 채팅방이 존재하지 않는 경우, 채팅방을 새로 생성해 해당 채팅방의 uuid, 상대 회원 정보 등을 반환합니다.")
    @Parameter(name = "memberId", description = "채팅방을 시작할 대상 회원의 id 입니다.")
    @GetMapping("/chat/start/member/{memberId}")
    public ApiResponse<EnterChatroomResponse> startChatroomByMemberId(
            @PathVariable(name = "memberId") Long targetMemberId,
            @AuthMember Member member) {
        return ApiResponse.ok(chatFacadeService.startChatroomByMemberId(member, targetMemberId));
    }

}
