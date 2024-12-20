package com.gamegoo.gamegoo_v2.riot.service;

import com.gamegoo.gamegoo_v2.exception.RiotException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.riot.dto.RiotVerifyExistUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiotFacadeService {

    private final RiotAccountService riotAccountService;

    public void verifyRiotAccount(RiotVerifyExistUserRequest request) {

        // 1. puuid 발급 가능한지 검증
        String puuid = riotAccountService.getPuuid(request.getGameName(), request.getTag());

        if (puuid == null) {
            throw new RiotException(ErrorCode.RIOT_NOT_FOUND);
        }

        // 2. summonerid 발급 가능한지 검증
        String summonerId = riotAccountService.getSummonerId(puuid);

        if (summonerId == null) {
            throw new RiotException(ErrorCode.RIOT_NOT_FOUND);
        }
    }

}
