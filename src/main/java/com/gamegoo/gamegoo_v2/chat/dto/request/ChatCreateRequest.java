package com.gamegoo.gamegoo_v2.chat.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatCreateRequest {

    @NotEmpty(message = "message는 필수 값 입니다.")
    String message;

    @Valid
    SystemFlagRequest system;

}
