package com.gamegoo.gamegoo_v2.content.board.controller;

import com.gamegoo.gamegoo_v2.account.auth.annotation.AuthMember;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.content.board.dto.request.BoardRequest;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardResponse;
import com.gamegoo.gamegoo_v2.content.board.service.BoardService;
import com.gamegoo.gamegoo_v2.core.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 새로운 BoardController 예시
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2/posts")
@Tag(name = "Board", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 작성 API
     */
    @PostMapping("")
    @Operation(summary = "게시판 글 작성 API",
            description = "게시판에서 글을 작성하는 API 입니다. 게임 모드 1~4, 포지션 0~5를 입력하세요. 게임스타일은 최대 3개까지 입력가능합니다.")
    public ApiResponse<BoardResponse.boardInsertResponseDTO> boardInsert(
            @AuthMember Member member,                              // ← @AuthMember 사용
            @RequestBody BoardRequest.boardInsertDTO request
    ) {

        BoardResponse.boardInsertResponseDTO responseDTO = boardService.createBoard(request, member);

        return ApiResponse.ok(responseDTO);
    }

}
