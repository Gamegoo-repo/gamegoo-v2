package com.gamegoo.gamegoo_v2.content.board.dto.response;

import com.gamegoo.gamegoo_v2.account.member.domain.Tier;
import com.gamegoo.gamegoo_v2.game.dto.response.ChampionResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BoardListResponse {

    long boardId;
    long memberId;
    Integer profileImage;
    String gameName;
    String tag;
    Integer mannerLevel;
    Tier tier;
    int rank;
    int gameMode;
    int mainPosition;
    int subPosition;
    int wantPosition;
    List<ChampionResponse> championResponseList;
    Double winRate;
    LocalDateTime createdAt;
    boolean mike;

}
