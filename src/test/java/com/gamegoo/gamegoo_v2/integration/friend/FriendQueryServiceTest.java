package com.gamegoo.gamegoo_v2.integration.friend;

import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.friend.service.FriendFacadeService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class FriendQueryServiceTest {

    @Autowired
    FriendFacadeService friendFacadeService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BlockRepository blockRepository;

    @Autowired
    FriendRepository friendRepository;

    private static final String MEMBER_EMAIL = "test@gmail.com";
    private static final String MEMBER_GAMENAME = "member";
    private static final String TARGET_EMAIL = "target@naver.com";
    private static final String TARGET_GAMENAME = "target";

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
    }

    @AfterEach
    void tearDown() {
        friendRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("모든 친구 id 조회")
    class GetFriendIdList {

        @DisplayName("모든 친구 id 조회 성공: 친구가 있는 경우")
        @Test
        void getFriendIdListSucceeds() {
            // given
            for (int i = 1; i <= 5; i++) {
                Member targetMember = createMember("member" + i + "@gmail.com", "member" + i);

                // 친구 관계 생성
                friendRepository.save(Friend.create(member, targetMember));
                friendRepository.save(Friend.create(targetMember, member));
            }

            // when
            List<Long> friendIdList = friendFacadeService.getFriendIdList(member);

            // then
            assertThat(friendIdList).hasSize(5);
        }

        @DisplayName("모든 친구 id 조회 성공: 친구가 없는 경우")
        @Test
        void getFriendIdListSucceedsWhenNoFriend() {
            // when
            List<Long> friendIdList = friendFacadeService.getFriendIdList(member);

            // then
            assertThat(friendIdList).hasSize(0);
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
