package com.gamegoo.gamegoo_v2.repository.friend;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.repository.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FriendRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private FriendRepository friendRepository;

    private static final int PAGE_SIZE = 10;

    @Nested
    @DisplayName("친구 목록 조회")
    class FindFriendsByCursorTest {

        @DisplayName("친구 목록 조회: 친구가 0명인 경우 빈 slice를 반환해야 한다.")
        @Test
        void findFriendsByCursorNoResult() {
            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursor(member.getId(), null, PAGE_SIZE);

            // then
            assertThat(friendSlice).isEmpty();
            assertThat(friendSlice.hasNext()).isFalse();
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이하이고 cursor로 null을 입력한 경우 첫 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorOnePage() {
            // given
            for (int i = 1; i <= 10; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursor(member.getId(), null, PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isFalse();
            assertThat(friendSlice.getContent().get(0).getToMember().getGameName()).isEqualTo("member1");
            assertThat(friendSlice.getContent().get(9).getToMember().getGameName()).isEqualTo("member9");
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이상이고 cursor로 null을 입력한 경우 첫 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorFirstPage() {
            // given
            List<Member> members = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
                members.add(toMember);
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursor(member.getId(), null, PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isEqualTo(true);
            assertThat(friendSlice.getContent().get(0).getToMember().getId()).isEqualTo(members.get(0).getId());
            assertThat(friendSlice.getContent().get(9).getToMember().getId()).isEqualTo(members.get(17).getId());
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이상이고 cursor를 정상 입력한 경우 다음 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorNextPage() {
            // given
            List<Member> members = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
                members.add(toMember);
            }

            Long cursor = members.get(17).getId();

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursor(member.getId(), cursor, PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isFalse();
            assertThat(friendSlice.getContent().get(0).getToMember().getId()).isEqualTo(members.get(18).getId());
            assertThat(friendSlice.getContent().get(9).getToMember().getId()).isEqualTo(members.get(8).getId());
        }

        @DisplayName("친구 목록 조회: 친구가 page size 이상이고 cursor에 해당하는 회원이 없는 경우 첫 페이지를 조회해야 한다.")
        @Test
        void findFriendsByCursorFirstPageWhenNoCursorMember() {
            // given
            List<Member> members = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                Member toMember = createMember("member" + i + "@gmail.com", "member" + i);
                createFriend(member, toMember);
                members.add(toMember);
            }

            Long cursor = 999999L;

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursor(member.getId(), cursor, PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isTrue();
            assertThat(friendSlice.getContent().get(0).getToMember().getId()).isEqualTo(members.get(0).getId());
            assertThat(friendSlice.getContent().get(9).getToMember().getId()).isEqualTo(members.get(17).getId());
        }

        @DisplayName("친구 목록 조회: 조회 결과는 친구 회원의 소환사명에 대해 한>영>숫자 순으로 정렬되어야 한다.")
        @Test
        void findFriendsByCursorOrderByGameName() {
            // given
            List<String> gameNameList = Arrays.asList("가", "가1", "가2", "가10", "가a", "가가", "a", "가a1", "가aa", "123");
            for (int i = 0; i < gameNameList.size(); i++) {
                Member toMember = createMember("member" + (i + 1) + "@gmail.com", gameNameList.get(i));
                createFriend(member, toMember);
            }

            // when
            Slice<Friend> friendSlice = friendRepository.findFriendsByCursor(member.getId(), null, PAGE_SIZE);

            // then
            assertThat(friendSlice).hasSize(10);
            assertThat(friendSlice.hasNext()).isFalse();
            List<String> orderedGameName = Arrays.asList("가", "가가", "가a", "가aa", "가a1", "가1", "가10", "가2", "a", "123");
            for (int i = 0; i < orderedGameName.size(); i++) {
                assertThat(friendSlice.getContent().get(i).getToMember().getGameName()).isEqualTo(orderedGameName.get(i));
            }
        }

    }

    @Nested
    @DisplayName("소환사명으로 친구 검색")
    class FindFriendsByQueryStringTest {

        @DisplayName("소환사명으로 친구 검색: 검색 결과가 없는 경우 빈 리스트를 반환해야 한다.")
        @Test
        void findFriendsByQueryStringSucceedsNoResult() {
            // given
            String query = "targetMember";

            // when
            List<Friend> friendList = friendRepository.findFriendsByQueryString(member.getId(), query);

            // then
            assertThat(friendList).isEmpty();
        }

        @DisplayName("소환사명으로 친구 검색 성공: 검색한 결과가 있는 경우 결과 리스트를 반환해야 한다.")
        @Test
        void findFriendsByQueryStringSucceeds() {
            // given
            Member targetMember1 = createMember("targetMember1@gmail.com", "targetMember");
            Member targetMember2 = createMember("targetMember2@gmail.com", "target");
            Member targetMember3 = createMember("targetMember@gmail.com", "target3");
            Member targetMember4 = createMember("targetMember@gmail.com", "t");
            Member targetMember5 = createMember("targetMember@gmail.com", "TARGET");

            createFriend(member, targetMember1);
            createFriend(member, targetMember2);
            createFriend(member, targetMember3);
            createFriend(member, targetMember4);
            createFriend(member, targetMember5);

            String query = "target";

            // when
            List<Friend> friendList = friendRepository.findFriendsByQueryString(member.getId(), query);

            // then
            assertThat(friendList).hasSize(3);
        }

    }

    private Friend createFriend(Member fromMember, Member toMember) {
        friendRepository.save(Friend.create(toMember, fromMember));
        return friendRepository.save(Friend.create(fromMember, toMember));
    }

}
