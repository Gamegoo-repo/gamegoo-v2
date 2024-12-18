package com.gamegoo.gamegoo_v2.repository.friend;

import com.gamegoo.gamegoo_v2.config.QuerydslConfig;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class FriendRepositoryTest {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private TestEntityManager em;

    private static final int PAGE_SIZE = 10;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember("test@gmail.com", "member");
    }

    @Nested
    @DisplayName("친구 목록 조회")
    class findFriendsByCursorAndOrderedTest {

        @DisplayName("친구 목록 조회: 친구가 0명인 경우")
        @Test
        void findFriendsByCursorAndOrderedWhenNoFriend() {
            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursorAndOrdered(member.getId(), null,
                    PAGE_SIZE);

            // then
            assertThat(friendSlice).isEmpty();
            assertFalse(friendSlice.hasNext());
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이하이고 cursor로 null을 입력한 경우")
        @Test
        void findFriendsByCursorAndOrderedOnePage() {
            // given
            for (int i = 1; i <= 10; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursorAndOrdered(member.getId(), null,
                    PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isEqualTo(false);
            assertThat(friendSlice.getContent().get(0).getToMember().getGameName()).isEqualTo("member1");
            assertThat(friendSlice.getContent().get(9).getToMember().getGameName()).isEqualTo("member9");
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이상이고 cursor로 null을 입력한 경우 첫 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorAndOrderedFirstPage() {
            // given
            for (int i = 1; i <= 20; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursorAndOrdered(member.getId(), null,
                    PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isEqualTo(true);
            assertThat(friendSlice.getContent().get(0).getToMember().getGameName()).isEqualTo("member1");
            assertThat(friendSlice.getContent().get(9).getToMember().getGameName()).isEqualTo("member18");
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이상이고 cursor를 정상 입력한 경우 다음 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorAndOrderedNextPage() {
            Long cursorId = 0L;
            // given
            for (int i = 1; i <= 20; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
                if (i == 18) {
                    cursorId = toMember.getId();
                }
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursorAndOrdered(member.getId(), cursorId,
                    PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isEqualTo(false);
            assertThat(friendSlice.getContent().get(0).getToMember().getGameName()).isEqualTo("member19");
            assertThat(friendSlice.getContent().get(9).getToMember().getGameName()).isEqualTo("member9");
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이상이고 cursor에 해당하는 회원이 없는 경우 첫 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorAndOrderedFirstPageWhenNoCursorMember() {
            // given
            for (int i = 1; i <= 20; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
            }
            Long cursorId = 100L;

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursorAndOrdered(member.getId(), cursorId,
                    PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isEqualTo(true);
            assertThat(friendSlice.getContent().get(0).getToMember().getGameName()).isEqualTo("member1");
            assertThat(friendSlice.getContent().get(9).getToMember().getGameName()).isEqualTo("member18");
        }

        @DisplayName("친구 목록 조회: 조회 결과는 친구 회원의 소환사명에 대해 한>영>숫자 순으로 정렬되어야 한다.")
        @Test
        void findFriendsByCursorAndOrderedByGameName() {
            // given
            List<String> gameNameList = Arrays.asList("가", "가1", "가2", "가10", "가a", "가가", "a", "가a1", "가aa", "123");
            for (int i = 0; i < gameNameList.size(); i++) {
                Member toMember = createMember("member" + (i + 1) + "@gmail.com", gameNameList.get(i));
                createFriend(member, toMember);
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursorAndOrdered(member.getId(), null,
                    PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isEqualTo(false);
            List<String> orderedGameName = Arrays.asList("가", "가가", "가a", "가aa", "가a1", "가1", "가10", "가2", "a", "123");
            for (int i = 0; i < orderedGameName.size(); i++) {
                assertThat(friendSlice.getContent().get(i).getToMember().getGameName()).isEqualTo(orderedGameName.get(i));
            }
        }

    }

    private Member createMember(String email, String gameName) {
        return em.persist(Member.builder()
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

    private Friend createFriend(Member fromMember, Member toMember) {
        friendRepository.save(Friend.create(toMember, fromMember));
        return friendRepository.save(Friend.create(fromMember, toMember));
    }

}
