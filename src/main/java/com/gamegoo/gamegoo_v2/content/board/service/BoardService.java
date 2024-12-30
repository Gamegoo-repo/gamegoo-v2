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
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 게시글 엔티티 생성 및 저장
     */
    @Transactional
    public Board createAndSaveBoard(BoardInsertRequest request, Member member) {
        int boardProfileImage = (request.getBoardProfileImage() != null)
                ? request.getBoardProfileImage()
                : member.getProfileImage();

        Board board = Board.create(
                member,
                request.getGameMode(),
                request.getMainPosition(),
                request.getSubPosition(),
                request.getWantPosition(),
                request.isMike(),
                request.getContents(),
                boardProfileImage
        );
        return boardRepository.save(board);
    }

    /**
     * 게시글 엔티티 조회
     */
    public Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

}
