package com.gamegoo.gamegoo_v2.account.member.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PositionRequest {

    @Min(value = 0, message = "메인 포지션의 값은 0이상이어야 합니다.")
    @Max(value = 5, message = "메인 포지션의 값은 5이하이어야 합니다.")
    int mainP;
    
    @Min(value = 0, message = "서브 포지션의 값은 0이상이어야 합니다.")
    @Max(value = 5, message = "서브 포지션의 값은 5이하이어야합니다.")
    int subP;

    @Min(value = 0, message = "원하는 포지션의 값은 0이상이어야 합니다.")
    @Max(value = 5, message = "원하는 포지션의 값은 5이하이어야합니다.")
    int wantP;

}
