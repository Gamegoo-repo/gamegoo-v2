package com.gamegoo.gamegoo_v2.chat.controller;

import com.gamegoo.gamegoo_v2.chat.service.ChatFacadeService;
import com.gamegoo.gamegoo_v2.core.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Chat Internal", description = "Chat 관련 internal API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/internal")
public class ChatInternalController {

    private final ChatFacadeService chatFacadeService;

    @Operation(summary = "채팅방 uuid 조회 API", description = "회원이 속한 채팅방의 uuid를 조회하는 API 입니다.")
    @GetMapping("/{memberId}/chatroom/uuid")
    public ApiResponse<List<String>> getChatroomUuid(@PathVariable(name = "memberId") Long memberId) {
        return ApiResponse.ok(chatFacadeService.getChatroomUuids(memberId));
    }

}
