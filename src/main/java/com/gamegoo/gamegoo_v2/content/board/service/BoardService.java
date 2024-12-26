package com.gamegoo.gamegoo_v2.content.board.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.service.MemberService;
import com.gamegoo.gamegoo_v2.content.board.domain.Board;
import com.gamegoo.gamegoo_v2.content.board.domain.BoardGameStyle;
import com.gamegoo.gamegoo_v2.content.board.dto.request.BoardRequest;
import com.gamegoo.gamegoo_v2.content.board.dto.response.BoardResponse;
import com.gamegoo.gamegoo_v2.content.board.repository.BoardRepository;
import com.gamegoo.gamegoo_v2.core.exception.BoardException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.game.repository.GameStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final GameStyleRepository gameStyleRepository;

    /**
     * 게시글 생성
     *
     * @param request  게시글 생성 요청 DTO
     * @param memberId 회원 ID
     * @return 게시글 생성 응답 DTO
     */

    public BoardResponse.boardInsertResponseDTO createBoard(BoardRequest.boardInsertDTO request, Long memberId) {
        Member member = memberService.findMember(memberId);

        // 게시글 작성 로직
        Board board = Board.create(
                member,
                request.getGameMode(),
                request.getMainPosition(),
                request.getSubPosition(),
                request.getWantPosition(),
                request.getMike(),
                request.getContents(),
                request.getBoardProfileImage() != null ? request.getBoardProfileImage() : member.getProfileImage()
        );

        boardRepository.save(board);

        // 응답 DTO 생성
        return BoardResponse.boardInsertResponseDTO.create(
                board.getId(),
                member.getId(),
                board.getProfileImage(),
                member.getGameName(),
                member.getTag(),
                member.getTier(),
                member.getRank(),
                board.getMode(),
                board.getMainPosition(),
                board.getSubPosition(),
                board.getWantPosition(),
                board.getMike(),
                board.getGameStyles().stream().map(BoardGameStyle::getId).collect(Collectors.toList()),
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

}
