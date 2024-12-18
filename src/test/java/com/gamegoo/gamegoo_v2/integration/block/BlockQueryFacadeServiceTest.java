package com.gamegoo.gamegoo_v2.integration.block;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.dto.BlockListResponse;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.block.service.BlockFacadeService;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class BlockQueryFacadeServiceTest {

    @Autowired
    private BlockFacadeService blockFacadeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BlockRepository blockRepository;

    private static final String MEMBER_EMAIL = "test@gmail.com";
    private static final String MEMBER_GAMENAME = "member";

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
    }

    @AfterEach
    void tearDown() {
        blockRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("차단한 회원 목록 조회")
    class GetBlockListTest {

        @DisplayName("차단한 회원 목록 조회 성공: 차단한 회원이 존재하는 경우")
        @Test
        void getBlockListSucceedsWhenBlockedMemberExists() {
            // given
            for (int i = 1; i <= 5; i++) {
                Member targetMember = createMember("member" + i + "@gmail.com", "member" + i);

                // 내가 상대를 차단 처리
                blockRepository.save(Block.create(member, targetMember));
            }

            // when
            BlockListResponse blockList = blockFacadeService.getBlockList(member, 1);

            // then
            assertThat(blockList.getListSize()).isEqualTo(5);

            BlockListResponse.BlockedMemberResponse blockedMemberResponse = blockList.getBlockedMemberList().get(0);
            assertThat(blockedMemberResponse.getName()).isEqualTo("member5");
        }

        @DisplayName("차단한 회원 목록 조회 성공: 차단한 회원이 없는 경우")
        @Test
        void getBlockListSucceedsWhenBlockedMemberNotExists() {
            // when
            BlockListResponse blockList = blockFacadeService.getBlockList(member, 1);

            // then
            assertThat(blockList.getListSize()).isEqualTo(0);
            assertThat(blockList.getBlockedMemberList()).isEmpty();
        }

    }

    private Member createMember(String email, String gameName) {
        return memberRepository.save(Member.builder()
                .email(email)
                .password("testPassword")
                .profileImage(1)
                .loginType(LoginType.GENERAL)
                .gameName(gameName)
                .tag("TAG")
                .tier(Tier.IRON)
                .gameRank(0)
                .winRate(0.0)
                .gameCount(0)
                .isAgree(true)
                .build());
    }

}
