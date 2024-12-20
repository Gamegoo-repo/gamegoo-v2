package com.gamegoo.gamegoo_v2.riot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class RiotMatchResponse {

    InfoDTO info;

    @Data
    @Getter
    @Setter
    public static class InfoDTO {

        private List<ParticipantDTO> participants;

    }

    @Data
    @Getter
    @Setter
    public static class ParticipantDTO {

        private String riotIdGameName;
        private String gameMode;
        private int championId;

    }

}
