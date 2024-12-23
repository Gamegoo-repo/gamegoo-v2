package com.gamegoo.gamegoo_v2.chat.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SystemFlagRequest {

    @Min(1)
    @Max(2)
    @NotNull
    Integer flag;

    @NotNull
    Long boardId;

}
