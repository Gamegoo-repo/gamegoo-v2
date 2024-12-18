package com.gamegoo.gamegoo_v2.integration.friend;

import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.dto.FriendInfoResponse;
import com.gamegoo.gamegoo_v2.friend.dto.FriendListResponse;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class FriendQueryFacadeServiceTest {

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

    @Nested
    @DisplayName("친구 목록 조회")
    class GetFriendList {

        @DisplayName("친구 목록 조회 성공: 친구가 없는 경우")
        @Test
        void getFriendListSucceedsWhenNoFriend() {
            // when
            FriendListResponse friends = friendFacadeService.getFriends(member, null);

            // then
            assertThat(friends.getListSize()).isEqualTo(0);
            assertThat(friends.getNextCursor()).isNull();
            assertThat(friends.isHasNext()).isEqualTo(false);
        }

        @DisplayName("친구 목록 조회 성공: 친구가 page size 이하인 경우")
        @Test
        void getFriendListSucceedsWhenOnePage() {
            // given
            for (int i = 1; i <= 5; i++) {
                Member targetMember = createMember("member" + i + "@gmail.com", "member" + i);

                // 친구 관계 생성
                friendRepository.save(Friend.create(member, targetMember));
                friendRepository.save(Friend.create(targetMember, member));
            }

            // when
            FriendListResponse friends = friendFacadeService.getFriends(member, null);

            // then
            assertThat(friends.getListSize()).isEqualTo(5);
            assertThat(friends.getNextCursor()).isNull();
            assertThat(friends.isHasNext()).isEqualTo(false);
        }

        @DisplayName("친구 목록 조회 성공: 친구가 page size 이상이고 cursor를 입력한 경우")
        @Test
        void getFriendListSucceedsWhenNextPage() {
            // given
            Long cursorId = 0L;
            for (int i = 1; i <= 15; i++) {
                Member targetMember = createMember("member" + i + "@gmail.com", "member" + i);

                // 친구 관계 생성
                friendRepository.save(Friend.create(member, targetMember));
                friendRepository.save(Friend.create(targetMember, member));

                if (i == 4) {
                    cursorId = targetMember.getId();
                }
            }

            // when
            FriendListResponse friends = friendFacadeService.getFriends(member, cursorId);

            // then
            assertThat(friends.getListSize()).isEqualTo(5);
            assertThat(friends.getNextCursor()).isNull();
            assertThat(friends.isHasNext()).isEqualTo(false);
        }

        @DisplayName("친구 목록 조회 성공: 친구가 page size 이상이고 cursor를 입력하지 않은 경우")
        @Test
        void getFriendListSucceedsFirstPage() {
            // given
            for (int i = 1; i <= 15; i++) {
                Member targetMember = createMember("member" + i + "@gmail.com", "member" + i);

                // 친구 관계 생성
                friendRepository.save(Friend.create(member, targetMember));
                friendRepository.save(Friend.create(targetMember, member));
            }

            // when
            FriendListResponse friends = friendFacadeService.getFriends(member, null);

            // then
            assertThat(friends.getListSize()).isEqualTo(10);
            assertThat(friends.getNextCursor()).isNotNull();
            assertThat(friends.isHasNext()).isEqualTo(true);
        }

    }

    @Nested
    @DisplayName("소환사명으로 친구 검색")
    class searchFriendByGamename {

        @DisplayName("소환사명으로 친구 검색 성공: 친구가 없는 경우")
        @Test
        void searchFriendByGamenameSucceedsNoResult() {
            // given
            String query = "targetMember";

            // when
            List<FriendInfoResponse> friendList = friendFacadeService.searchFriend(member, query);

            // then
            assertThat(friendList).isEmpty();
        }

        @DisplayName("소환사명으로 친구 검색 성공: 친구가 있는 경우")
        @Test
        void searchFriendByGamenameSucceeds() {
            // given
            String query = "target";

            Member targetMember = createMember("targetMember@gmail.com", "targetMember");

            // 친구 관계 생성
            friendRepository.save(Friend.create(member, targetMember));
            friendRepository.save(Friend.create(targetMember, member));

            // when
            List<FriendInfoResponse> friendList = friendFacadeService.searchFriend(member, query);

            // then
            assertThat(friendList).hasSize(1);
        }

        @DisplayName("소환사명으로 친구 검색 실패: query 길이 제한을 초과한 경우 예외가 발생한다.")
        @Test
        void searchFriendByGamename_shouldThrowWhenQueryTooLong() {
            // given
            String query = "a".repeat(101);

            // when // then
            assertThatThrownBy(() -> friendFacadeService.searchFriend(member, query))
                    .isInstanceOf(FriendException.class)
                    .hasMessage(ErrorCode.FRIEND_SEARCH_QUERY_BAD_REQUEST.getMessage());
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
