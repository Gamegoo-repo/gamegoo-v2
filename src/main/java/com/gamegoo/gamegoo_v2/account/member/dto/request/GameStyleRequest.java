package com.gamegoo.gamegoo_v2.account.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class GameStyleRequest {

    @NotBlank(message = "gameStyleList은 비워둘 수 없습니다.")
    List<Long> gameStyleIdList;

}
