package com.gamegoo.gamegoo_v2.content.board.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.domain.MemberChampion;
import com.gamegoo.gamegoo_v2.account.member.domain.Tier;
import com.gamegoo.gamegoo_v2.content.board.domain.Board;
import com.gamegoo.gamegoo_v2.content.board.dto.request.BoardInsertRequest;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardInsertResponse;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardListResponse;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardResponse;
import com.gamegoo.gamegoo_v2.core.exception.BoardException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.game.dto.response.ChampionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardFacadeService {

    private final BoardService boardService;
    private final BoardGameStyleService boardGameStyleService;

    /**
     * 게시글 생성 (파사드)
     * - DTO -> 엔티티 변환 및 저장
     * - 연관된 GameStyle(BoardGameStyle) 매핑 처리
     * - 결과를 BoardInsertResponse로 변환하여 반환
     */
    @Transactional
    public BoardInsertResponse createBoard(BoardInsertRequest request, Member member) {

        Board board = boardService.createAndSaveBoard(request, member);
        boardGameStyleService.mapGameStylesToBoard(board, request.getGameStyles());

        return BoardInsertResponse.of(board, member);
    }

    /**
     * 게시판 글 목록 조회 (파사드)
     */
    public BoardResponse getBoardList(Integer mode, Tier tier, Integer mainPosition, Boolean mike, int pageIdx) {
        // 페이징 인덱스 검증
        if (pageIdx <= 0) {
            throw new BoardException(ErrorCode.BOARD_PAGE_BAD_REQUEST);
        }

        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(pageIdx - 1, BoardService.PAGE_SIZE, Sort.by(Sort.Direction.DESC,
                "createdAt"));

        // 게시글 목록 조회
        Page<Board> boardPage = boardService.findBoards(mode, tier, mainPosition, mike, pageable);
        List<Board> boards = boardPage.getContent();

        // 전체 페이지, 전체 개수 구하기
        int totalCount = (int) boardPage.getTotalElements();
        int totalPage = boardPage.getTotalPages() == 0 ? 1 : boardPage.getTotalPages();

        // Board 엔티티 -> BoardListResponse로 변환
        List<BoardListResponse> boardList = boards.stream()
                .map(this::mapToBoardListResponse)
                .collect(Collectors.toList());

        // 결과 반환
        return BoardResponse.builder()
                .totalPage(totalPage)
                .totalCount(totalCount)
                .boards(boardList)
                .build();
    }


    /**
     * 단일 Board -> BoardListResponse 변환
     */
    private BoardListResponse mapToBoardListResponse(Board board) {
        Member member = board.getMember();

        // MemberChampion -> ChampionResponse 변환
        List<ChampionResponse> championResponseList = mapToChampionResponseList(member.getMemberChampionList());

        return BoardListResponse.builder()
                .boardId(board.getId())
                .memberId(member.getId())
                .profileImage(board.getBoardProfileImage())
                .gameName(member.getGameName())
                .tag(member.getTag())
                .mannerLevel(member.getMannerLevel())
                .tier(member.getTier() == null ? null : member.getTier())
                .rank(member.getGameRank())
                .gameMode(board.getMode())
                .mainPosition(board.getMainPosition())
                .subPosition(board.getSubPosition())
                .wantPosition(board.getWantPosition())
                .championResponseList(championResponseList)
                .winRate(member.getWinRate())
                .createdAt(board.getCreatedAt())
                .mike(board.isMike())
                .build();
    }

    /**
     * MemberChampion -> ChampionResponse 변환
     */
    private List<ChampionResponse> mapToChampionResponseList(List<MemberChampion> memberChampions) {
        if (memberChampions == null) {
            return null;
        }
        return memberChampions.stream()
                .map(mc -> ChampionResponse.builder()
                        .championId(mc.getChampion().getId())
                        .championName(mc.getChampion().getName())
                        .build())
                .collect(Collectors.toList());
    }

}
