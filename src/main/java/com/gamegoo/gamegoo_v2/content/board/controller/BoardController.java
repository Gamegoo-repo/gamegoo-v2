package com.gamegoo.gamegoo_v2.content.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class BoardController {
    @PostMapping
    @Operation(summary = "게시판 글 작성 API", description = "게시판에 새로운 글을 작성합니다.")
    public ApiResponse<BoardResponse.boardInsertResponseDTO> boardInsert(@RequestBody BoardRequest.boardInsertDTO request) {
        Long memberId = JWTUtil.getCurrentUserId(); // JWT로 현재 사용자 식별
        BoardResponse.boardInsertResponseDTO response = boardService.createBoard(request, memberId);
        return ApiResponse.onSuccess(response);
    }


}
