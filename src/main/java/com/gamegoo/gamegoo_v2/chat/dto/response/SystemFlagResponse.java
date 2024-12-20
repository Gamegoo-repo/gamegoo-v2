package com.gamegoo.gamegoo_v2.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SystemFlagResponse {

    int flag;
    Long boardId;

    public static SystemFlagResponse of(int flag, Long boardId) {
        return SystemFlagResponse.builder()
                .flag(flag)
                .boardId(boardId)
                .build();
    }

}
