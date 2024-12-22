package com.gamegoo.gamegoo_v2.auth.service;

import com.gamegoo.gamegoo_v2.auth.dto.JoinRequest;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import com.gamegoo.gamegoo_v2.riot.service.RiotAccountService;
import com.gamegoo.gamegoo_v2.riot.service.RiotInfoService;
import com.gamegoo.gamegoo_v2.riot.service.RiotRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthFacadeService {

    private final AuthService authService;
    private final MemberService memberService;
    private final RiotAccountService riotAccountService;
    private final RiotRecordService riotRecordService;
    private final RiotInfoService riotInfoService;

    /**
     * 회원가입
     *
     * @param request 회원가입용 정보
     */
    @Transactional
    public void join(JoinRequest request) {
        // 1. [Member] 중복확인
        memberService.checkExistMemberByEmail(request.getEmail());

        // 2. [Riot] 존재하는 소환사인지 검증 & puuid 얻기
        String puuid = riotAccountService.getPuuid(request.getGameName(), request.getTag());

        // 3. [Riot] tier, rank, winrate 얻기

        // 4. [Member] member DB에 저장

        // 5. [Riot] 최근 사용한 챔피언 3개 매핑

    }

}
