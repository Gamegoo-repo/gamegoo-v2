package com.gamegoo.gamegoo_v2.riot.dto;

import com.gamegoo.gamegoo_v2.member.domain.Tier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TierDetails {

    Tier tier;
    double winrate;
    int rank;

}
