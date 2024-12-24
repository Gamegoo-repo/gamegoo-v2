package com.gamegoo.gamegoo_v2.integration.member;

import com.gamegoo.gamegoo_v2.account.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.domain.MemberChampion;
import com.gamegoo.gamegoo_v2.account.member.domain.Tier;
import com.gamegoo.gamegoo_v2.account.member.dto.response.MyProfileResponse;
import com.gamegoo.gamegoo_v2.account.member.dto.response.OtherProfileResponse;
import com.gamegoo.gamegoo_v2.account.member.repository.MemberRepository;
import com.gamegoo.gamegoo_v2.account.member.service.MemberChampionService;
import com.gamegoo.gamegoo_v2.account.member.service.MemberFacadeService;
import com.gamegoo.gamegoo_v2.game.domain.Champion;
import com.gamegoo.gamegoo_v2.game.repository.ChampionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceFacadeTest {

    @Autowired
    MemberFacadeService memberFacadeService;

    @Autowired
    MemberChampionService memberChampionService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChampionRepository championRepository;

    private static Member member;
    private static Member targetMember;

    @BeforeEach
    void setUp() {
        // Member 테스트용 객체 생성
        member = Member.create("test@gmail.com", "pwd", LoginType.GENERAL, "TEST_GAMENAME", "TEST_TAG",
                Tier.DIAMOND, 1, 50.0, 100, true);

        // targetMember 테스트용 객체 생성
        targetMember = Member.create("target@gmail.com", "pwd", LoginType.GENERAL, "TARGET_GAMENAME", "TARGET_TAG",
                Tier.PLATINUM, 2, 40.0, 80, false);

        memberRepository.save(member);
        memberRepository.save(targetMember);

        // Champion 테스트용 객체 생성
        Champion annie = Champion.create(1l, "Annie");
        Champion olaf = Champion.create(2l, "Olaf");
        Champion galio = Champion.create(3l, "Galio");

        championRepository.saveAll(List.of(annie, olaf, galio));

        List<Long> championIds = Arrays.asList(1L, 2L, 3L);
        memberChampionService.saveMemberChampions(member, championIds);
        memberChampionService.saveMemberChampions(targetMember, championIds);
    }

    @Nested
    @DisplayName("프로필 조회")
    class GetProfileTest {

        @DisplayName("내 프로필 조회 성공")
        @Test
        void getProfile() {
            // when
            MyProfileResponse response = memberFacadeService.getMyProfile(member);

            // then
            // TODO: GameStyle 로직 추가
            // TODO: GameStyle 로직 추가
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(member.getId());
            assertThat(response.getProfileImg()).isEqualTo(member.getProfileImage());
            assertThat(response.getMike()).isEqualTo(member.isMike());
            assertThat(response.getEmail()).isEqualTo(member.getEmail());
            assertThat(response.getGameName()).isEqualTo(member.getGameName());
            assertThat(response.getTag()).isEqualTo(member.getTag());
            assertThat(response.getTier()).isEqualTo(member.getTier());
            assertThat(response.getGameRank()).isEqualTo(member.getGameRank());
            assertThat(response.getWinrate()).isEqualTo(member.getWinRate());
            assertThat(response.getMainP()).isEqualTo(member.getMainPosition());
            assertThat(response.getSubP()).isEqualTo(member.getSubPosition());
            assertThat(response.getWantP()).isEqualTo(member.getWantPosition());
            assertThat(response.getIsAgree()).isEqualTo(member.isAgree());
            assertThat(response.getIsBlind()).isEqualTo(member.isBlind());
            assertThat(response.getLoginType()).isEqualTo(member.getLoginType().name());
            assertThat(response.getMannerLevel()).isEqualTo(member.getMannerLevel());
            assertThat(response.getChampionResponseList()).isNotNull();
            assertThat(response.getChampionResponseList()).hasSize(3);

            List<Long> championIds =
                    member.getMemberChampionList().stream().map(MemberChampion::getId).toList();

            for (int i = 0; i < championIds.size(); i++) {
                assertThat(response.getChampionResponseList().get(i).getChampionId()).isEqualTo(championIds.get(i));
            }

        }

        @DisplayName("다른 사람 프로필 조회 성공")
        @Test
        void getOtherProfile() {
            // when
            OtherProfileResponse response = memberFacadeService.getOtherProfile(member, targetMember.getId());

            // then
            // TODO: 차단 확인 테스트 추가
            // TODO: 친구 확인 테스트 추가
            // TODO: GameStyle 로직 추가 후 수정 필요
            // TODO: Manner 관련 로직 추가
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(targetMember.getId());
            assertThat(response.getProfileImg()).isEqualTo(targetMember.getProfileImage());
            assertThat(response.getMike()).isEqualTo(targetMember.isMike());
            assertThat(response.getGameName()).isEqualTo(targetMember.getGameName());
            assertThat(response.getTag()).isEqualTo(targetMember.getTag());
            assertThat(response.getTier()).isEqualTo(targetMember.getTier());
            assertThat(response.getRank()).isEqualTo(targetMember.getGameRank());
            assertThat(response.getMannerLevel()).isEqualTo(targetMember.getMannerLevel());
            assertThat(response.getMainP()).isEqualTo(targetMember.getMainPosition());
            assertThat(response.getSubP()).isEqualTo(targetMember.getSubPosition());
            assertThat(response.getWantP()).isEqualTo(targetMember.getWantPosition());
            assertThat(response.getIsAgree()).isEqualTo(targetMember.isAgree());
            assertThat(response.getIsBlind()).isEqualTo(targetMember.isBlind());
            assertThat(response.getLoginType()).isEqualTo(String.valueOf(targetMember.getLoginType()));
            assertThat(response.getWinrate()).isEqualTo(targetMember.getWinRate());
            assertThat(response.getChampionResponseList()).isNotNull();
            assertThat(response.getChampionResponseList()).hasSize(3);

            List<Champion> championList =
                    targetMember.getMemberChampionList().stream().map(MemberChampion::getChampion).toList();
            List<Long> championIds = championList.stream().map(Champion::getId).toList();

            for (int i = 0; i < championIds.size(); i++) {
                assertThat(response.getChampionResponseList().get(i).getChampionId()).isEqualTo(championIds.get(i));
            }

        }

    }

}
