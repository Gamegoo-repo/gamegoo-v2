package com.gamegoo.gamegoo_v2.content.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class BoardRequest {

    @Getter
    @Setter
    public static class boardInsertDTO {

        Integer boardProfileImage;
        @NotNull
        Integer gameMode;

        Integer mainPosition;

        Integer subPosition;

        Integer wantPosition;

        @Schema(description = "마이크 사용 여부", defaultValue = "false")
        Boolean mike = false;
        List<Long> gameStyles;
        String contents;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class boardUpdateDTO {

        Integer boardProfileImage;
        Integer gameMode;

        Integer mainPosition;

        Integer subPosition;

        Integer wantPosition;
        Boolean mike;
        List<Long> gameStyles;
        String contents;

    }

}
