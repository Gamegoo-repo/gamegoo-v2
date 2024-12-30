package com.gamegoo.gamegoo_v2.content.board.dto.response;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.content.board.domain.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardInsertResponse {

    private long boardId;
    private long memberId;
    private Integer profileImage;
    private String gameName;
    private String tag;
    private String tier;
    private int rank;
    private int gameMode;
    private int mainPosition;
    private int subPosition;
    private int wantPosition;
    private boolean mike;
    private List<Long> gameStyles;
    private String contents;

    public static BoardInsertResponse of(Board board, Member member) {
        return BoardInsertResponse.builder()
                .boardId(board.getId())
                .memberId(member.getId())
                .profileImage(board.getBoardProfileImage())
                .gameName(member.getGameName())
                .tag(member.getTag())
                .tier(member.getTier().name())
                .rank(member.getGameRank())
                .gameMode(board.getMode())
                .mainPosition(board.getMainPosition())
                .subPosition(board.getSubPosition())
                .wantPosition(board.getWantPosition())
                .mike(board.isMike())
                .gameStyles(board.getBoardGameStyles().stream()
                        .map(bg -> bg.getGameStyle().getId())
                        .toList())
                .contents(board.getContent())
                .build();
    }

}
