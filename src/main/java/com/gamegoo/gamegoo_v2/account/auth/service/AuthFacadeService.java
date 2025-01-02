package com.gamegoo.gamegoo_v2.account.auth.service;

import com.gamegoo.gamegoo_v2.account.auth.dto.request.JoinRequest;
import com.gamegoo.gamegoo_v2.account.auth.dto.request.LoginRequest;
import com.gamegoo.gamegoo_v2.account.auth.dto.response.LoginResponse;
import com.gamegoo.gamegoo_v2.account.auth.jwt.JwtProvider;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.service.MemberChampionService;
import com.gamegoo.gamegoo_v2.account.member.service.MemberService;
import com.gamegoo.gamegoo_v2.external.riot.dto.TierDetails;
import com.gamegoo.gamegoo_v2.external.riot.service.RiotAuthService;
import com.gamegoo.gamegoo_v2.external.riot.service.RiotInfoService;
import com.gamegoo.gamegoo_v2.external.riot.service.RiotRecordService;
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
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final PasswordService passwordService;

    /**
     * 회원가입
     *
     * @param request 회원가입용 정보
     */
    @Transactional
    public String join(JoinRequest request) {
        // 1. [Member] 중복확인
        memberService.checkDuplicateMemberByEmail(request.getEmail());

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
        List<Long> preferChampionfromMatch = riotRecordService.getPreferChampionfromMatch(request.getGameName(),
                puuid);

        // 6. [Member] Member Champion DB에서 매핑하기
        memberChampionService.saveMemberChampions(member, preferChampionfromMatch);

        return "회원가입이 완료되었습니다.";
    }

    public LoginResponse login(LoginRequest request) {
        // email 검증
        Member member = memberService.findMemberByEmail(request.getEmail());

        // password 검증
        passwordService.verifyPassword(member, request.getPassword());

        // 해당 사용자의 정보를 가진 jwt 토큰 발급
        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        // refreshToken 저장
        authService.addRefreshToken(member, refreshToken);

        return LoginResponse.of(member, accessToken, refreshToken);
    }

    @Transactional
    public String logout(Member member) {
        authService.deleteRefreshToken(member);
        return "로그아웃이 완료되었습니다.";
    }

}
