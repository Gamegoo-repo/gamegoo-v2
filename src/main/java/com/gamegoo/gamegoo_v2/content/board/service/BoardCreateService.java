package com.gamegoo.gamegoo_v2.content.board.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.content.board.domain.Board;
import com.gamegoo.gamegoo_v2.content.board.dto.request.BoardInsertRequest;
import com.gamegoo.gamegoo_v2.content.board.repository.BoardRepository;
import com.gamegoo.gamegoo_v2.core.exception.BoardException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCreateService {

    private final BoardRepository boardRepository;

    @Transactional
    public Board createBoard(BoardInsertRequest request, Member member) {
        int boardProfileImage = (request.getBoardProfileImage() != null)
                ? request.getBoardProfileImage()
                : member.getProfileImage();

        return Board.create(
                member,
                request.getGameMode(),
                request.getMainPosition(),
                request.getSubPosition(),
                request.getWantPosition(),
                request.getMike() != null && request.getMike(),
                request.getContents(),
                boardProfileImage
        );
    }

    /**
     * Board 엔티티 DB 저장
     */
    @Transactional
    public void saveBoard(Board board) {
        boardRepository.save(board);
    }

    /**
     * 게시글 엔티티 조회
     * 추후 BoardQueryService로 이동
     */
    public Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

}
