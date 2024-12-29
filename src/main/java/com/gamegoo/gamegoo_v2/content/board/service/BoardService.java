package com.gamegoo.gamegoo_v2.content.board.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.service.MemberService;
import com.gamegoo.gamegoo_v2.content.board.domain.Board;
import com.gamegoo.gamegoo_v2.content.board.domain.BoardGameStyle;
import com.gamegoo.gamegoo_v2.content.board.dto.request.BoardRequest;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardResponse;
import com.gamegoo.gamegoo_v2.content.board.repository.BoardGameStyleRepository;
import com.gamegoo.gamegoo_v2.content.board.repository.BoardRepository;
import com.gamegoo.gamegoo_v2.core.exception.BoardException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.game.domain.GameStyle;
import com.gamegoo.gamegoo_v2.game.repository.GameStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final GameStyleRepository gameStyleRepository;
    private final BoardGameStyleRepository boardGameStyleRepository;

    /**
     * 게시글 생성
     *
     * @param request 게시글 생성 요청 DTO
     * @param member  멤버 객체
     * @return 게시글 생성 응답 DTO
     */

    @Transactional
    public BoardResponse.boardInsertResponseDTO createBoard(BoardRequest.boardInsertDTO request, Member member) {

        validateGameMode(request.getGameMode());
        validateGameStyles(request.getGameStyles());
        validatePositions(request.getMainPosition(), request.getSubPosition(), request.getWantPosition());

        // 게시글 작성
        Board board = Board.create(
                member,
                request.getGameMode(),
                request.getMainPosition(),
                request.getSubPosition(),
                request.getWantPosition(),
                request.getMike(),
                request.getContents(),
                // 프로필 이미지 설정
                request.getBoardProfileImage() != null ? request.getBoardProfileImage() : member.getProfileImage()
        );


        // 게시글 게임 스타일 저장
        List<Long> gameStyleIds = request.getGameStyles();
        List<BoardGameStyle> boardGameStyles = gameStyleIds.stream()
                .map(gameStyleId -> {
                    GameStyle gameStyle = gameStyleRepository.findById(gameStyleId)
                            .orElseThrow(() -> new BoardException(ErrorCode.BOARD_GAME_STYLE_BAD_REQUEST));
                    return BoardGameStyle.create(gameStyle, board);
                })
                .collect(Collectors.toList());

        boardGameStyles.forEach(board::addBoardGameStyle);
        boardRepository.save(board);

        // 응답 DTO 생성
        return BoardResponse.boardInsertResponseDTO.create(
                board.getId(),
                member.getId(),
                board.getBoardProfileImage(),
                member.getGameName(),
                member.getTag(),
                member.getTier(),
                member.getGameRank(),
                board.getMode(),
                board.getMainPosition(),
                board.getSubPosition(),
                board.getWantPosition(),
                board.isMike(),
                boardGameStyles.stream().map(bg -> bg.getGameStyle().getId()).collect(Collectors.toList()),
                board.getContent()
        );
    }


    /**
     * 게시글 엔티티 조회
     *
     * @param boardId
     * @return
     */
    public Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

    /**
     * 게임 스타일 검증
     *
     * @param gameStyles 게임 스타일 ID 목록
     */
    private void validateGameStyles(List<Long> gameStyles) {
        if (gameStyles.size() > 3) {
            throw new BoardException(ErrorCode.BOARD_GAME_STYLE_BAD_REQUEST);
        }
    }

    /**
     * 게임 모드 검증
     *
     * @param gameMode
     */
    private void validateGameMode(int gameMode) {
        if (gameMode < 1 || gameMode > 4) {
            throw new BoardException(ErrorCode.BOARD_GAME_MODE_BAD_REQUEST);
        }
    }

    /**
     * 게임 포지션 검증
     *
     * @param mainPosition
     * @param subPosition
     * @param wantPosition
     */
    private void validatePositions(Integer mainPosition, Integer subPosition, Integer wantPosition) {
        if (mainPosition == null || mainPosition < 0 || mainPosition > 5) {
            throw new BoardException(ErrorCode.BOARD_MAIN_POSITION_BAD_REQUEST);
        }
        if (subPosition == null || subPosition < 0 || subPosition > 5) {
            throw new BoardException(ErrorCode.BOARD_SUB_POSITION_BAD_REQUEST);
        }
        if (wantPosition == null || wantPosition < 0 || wantPosition > 5) {
            throw new BoardException(ErrorCode.BOARD_WANT_POSITION_BAD_REQUEST);
        }
        // 주 포지션과 부 포지션 중복 검증
        if (mainPosition.equals(subPosition)) {
            throw new BoardException(ErrorCode.BOARD_MAIN_SUB_POSITION_DUPLICATE);
        }
    }


}
