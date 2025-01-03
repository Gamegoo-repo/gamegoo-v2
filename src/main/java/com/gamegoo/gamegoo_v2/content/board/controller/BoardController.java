package com.gamegoo.gamegoo_v2.content.board.controller;

import com.gamegoo.gamegoo_v2.account.auth.annotation.AuthMember;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.domain.Tier;
import com.gamegoo.gamegoo_v2.content.board.dto.request.BoardInsertRequest;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardInsertResponse;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardResponse;
import com.gamegoo.gamegoo_v2.content.board.service.BoardFacadeService;
import com.gamegoo.gamegoo_v2.core.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/posts")
@Tag(name = "Board", description = "게시판 관련 API")
public class BoardController {

    private final BoardFacadeService boardFacadeService;

    /**
     * 게시글 작성 API
     */
    @PostMapping
    @Operation(summary = "게시판 글 작성 API",
            description = "게시판에서 글을 작성하는 API 입니다. 게임 모드 1~4, 포지션 0~5를 입력하세요. 게임스타일은 최대 3개까지 입력가능합니다.")
    public ApiResponse<BoardInsertResponse> boardInsert(
            @AuthMember Member member,
            @Valid @RequestBody BoardInsertRequest request) {
        return ApiResponse.ok(boardFacadeService.createBoard(request, member));
    }

    /**
     * 게시글 목록 조회 API
     */
    @GetMapping("/list")
    @Operation(summary = "게시판 글 목록 조회 API",
            description = "게시판 글 목록을 조회하는 API 입니다. 필터링을 원하면 각 파라미터를 입력하세요.")
    @Parameters({
            @Parameter(name = "pageIdx", description = "조회할 페이지 번호를 입력해주세요. 페이지 당 20개의 게시물을 볼 수 있습니다."),
            @Parameter(name = "mode", description = "(선택) 게임 모드를 입력해주세요. < 빠른대전: 1, 솔로랭크: 2, 자유랭크: 3, 칼바람 나락: 4 >"),
            @Parameter(name = "tier", description = "(선택) 티어를 선택해주세요."),
            @Parameter(name = "mainPosition", description = "(선택) 포지션을 입력해주세요. < 전체: 0, 탑: 1, 정글: 2, 미드: 3, 바텀: 4, " +
                    "서포터: 5 >"),
            @Parameter(name = "mike", description = "(선택) 마이크 여부를 선택해주세요.")
    })
    public ApiResponse<BoardResponse> boardList(
            @RequestParam(defaultValue = "1") int pageIdx,
            @RequestParam(required = false) Integer mode,
            @RequestParam(required = false) Tier tier,
            @RequestParam(required = false) Integer mainPosition,
            @RequestParam(required = false) Boolean mike) {
        // <포지션 정보> 전체: 0, 탑: 1, 정글: 2, 미드: 3, 바텀: 4, 서포터: 5
        if (mainPosition != null && mainPosition == 0) {
            // 전체 포지션 선택 시 필터링에서 제외
            mainPosition = null;
        }

        BoardResponse response = boardFacadeService.getBoardList(mode, tier, mainPosition, mike, pageIdx);
        return ApiResponse.ok(response);
    }


}
