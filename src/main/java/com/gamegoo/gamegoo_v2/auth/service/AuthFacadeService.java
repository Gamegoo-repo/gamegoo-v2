package com.gamegoo.gamegoo_v2.auth.service;

import com.gamegoo.gamegoo_v2.auth.dto.JoinRequest;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.service.MemberChampionService;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import com.gamegoo.gamegoo_v2.riot.dto.TierDetails;
import com.gamegoo.gamegoo_v2.riot.service.RiotAuthService;
import com.gamegoo.gamegoo_v2.riot.service.RiotInfoService;
import com.gamegoo.gamegoo_v2.riot.service.RiotRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthFacadeService {

    private final MemberService memberService;
    private final RiotAuthService riotAccountService;
    private final RiotRecordService riotRecordService;
    private final RiotInfoService riotInfoService;
    private final MemberChampionService memberChampionService;

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

        // 3. [Riot] summonerId 얻기
        String summonerId = riotAccountService.getSummonerId(puuid);

        // 3. [Riot] tier, rank, winrate 얻기
        TierDetails tierWinrateRank = riotInfoService.getTierWinrateRank(summonerId);

        // 4. [Member] member DB에 저장
        Member member = memberService.createMember(request.getEmail(), request.getPassword(), request.getGameName(),
                request.getTag(), tierWinrateRank.getTier(), tierWinrateRank.getRank(), tierWinrateRank.getWinrate(),
                tierWinrateRank.getGameCount(), request.getIsAgree());

        // 5. [Riot] 최근 사용한 챔피언 3개 가져오기
        List<Integer> preferChampionfromMatch = riotRecordService.getPreferChampionfromMatch(request.getGameName(),
                puuid);

        // 6. [Member] Member Champion DB에서 매핑하기
        memberChampionService.saveMemberChampions(member, preferChampionfromMatch);
    }

}
