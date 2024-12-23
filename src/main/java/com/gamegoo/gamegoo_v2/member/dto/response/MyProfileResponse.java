package com.gamegoo.gamegoo_v2.member.dto.response;

import com.gamegoo.gamegoo_v2.game.dto.response.ChampionResponse;
import com.gamegoo.gamegoo_v2.game.dto.response.GameStyleResponse;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyProfileResponse {

    Long id;
    Integer profileImg;
    Boolean mike;
    String email;
    String gameName;
    String tag;
    Tier tier;
    Integer gameRank;
    Double mannerRank;
    Integer mannerLevel;
    String updatedAt;
    Integer mainP;
    Integer subP;
    Integer wantP;
    Boolean isAgree;
    Boolean isBlind;
    String loginType;
    Double winrate;
    List<GameStyleResponse> gameStyleResponseList;
    List<ChampionResponse> championResponseList;

    public static MyProfileResponse of(Member member, Double mannerRank) {
        List<GameStyleResponse> gameStyleResponseList = member.getMemberGameStyleList().stream()
                .map(memberGameStyle -> GameStyleResponse.of(memberGameStyle.getGameStyle()))
                .toList();

        List<ChampionResponse> championResponseList = member.getMemberChampionList().stream()
                .map(memberChampion -> ChampionResponse.of(memberChampion.getChampion()))
                .toList();

        return MyProfileResponse.builder()
                .id(member.getId())
                .mike(member.isMike())
                .email(member.getEmail())
                .gameName(member.getGameName())
                .tag(member.getTag())
                .tier(member.getTier())
                .gameRank(member.getGameRank())
                .profileImg(member.getProfileImage())
                .mannerLevel(member.getMannerLevel())
                .mannerRank(mannerRank)
                .mainP(member.getMainPosition())
                .subP(member.getSubPosition())
                .wantP(member.getWantPosition())
                .isAgree(member.isAgree())
                .isBlind(member.isBlind())
                .winrate(member.getWinRate())
                .loginType(String.valueOf(member.getLoginType()))
                .updatedAt(String.valueOf(member.getUpdatedAt()))
                .gameStyleResponseList(gameStyleResponseList)
                .championResponseList(championResponseList)
                .build();
    }

}
