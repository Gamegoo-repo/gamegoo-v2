package com.gamegoo.gamegoo_v2.integration.friend;

import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.dto.StarFriendResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
class FriendServiceTest {

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
    @DisplayName("친구 즐겨찾기 설정/해제")
    class ReverseFriendLikedTest {

        @DisplayName("친구 즐겨찾기 설정 성공: 즐겨찾기 되지 않은 친구를 요청한 경우 즐겨찾기가 설정된다.")
        @Test
        void reverseFriendLikedSucceedsWhenNotLiked() {
            // given
            Member targetMember = createMember(TARGET_EMAIL, TARGET_GAMENAME);

            // 두 회원 간 친구 관계 생성
            friendRepository.save(Friend.create(member, targetMember));
            friendRepository.save(Friend.create(targetMember, member));

            // when
            StarFriendResponse response = friendFacadeService.reverseFriendLiked(member, targetMember.getId());

            // then
            Friend friend = friendRepository.findByFromMemberAndToMember(member, targetMember);
            assertTrue(friend.isLiked());
            assertThat(response.getMessage()).isEqualTo("친구 즐겨찾기 설정 성공");
        }

        @DisplayName("친구 즐겨찾기 해제 성공: 즐겨찾기 된 친구를 요청한 경우 즐겨찾기가 해제된다.")
        @Test
        void reverseFriendLikedSucceedsWhenLiked() {
            // given
            Member targetMember = createMember(TARGET_EMAIL, TARGET_GAMENAME);

            // 두 회원 간 친구 관계 생성
            Friend savedFriend = friendRepository.save(Friend.create(member, targetMember));
            friendRepository.save(Friend.create(targetMember, member));

            // member -> targetMember 즐겨찾기 설정
            savedFriend.reverseLiked();
            friendRepository.save(savedFriend);

            // when
            StarFriendResponse response = friendFacadeService.reverseFriendLiked(member, targetMember.getId());

            // then
            Friend friend = friendRepository.findByFromMemberAndToMember(member, targetMember);
            assertFalse(friend.isLiked());
            assertThat(response.getMessage()).isEqualTo("친구 즐겨찾기 해제 성공");
        }

        @DisplayName("친구 즐겨찾기 설정/해제 실패: 본인 id를 요청한 경우 예외가 발생한다.")
        @Test
        void reverseFriendLiked_shouldThrownWhenTargetIsSelf() {
            // when // then
            assertThatThrownBy(() -> friendFacadeService.reverseFriendLiked(member, member.getId()))
                    .isInstanceOf(FriendException.class)
                    .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
        }

        @DisplayName("친구 즐겨찾기 설정/해제 실패: 상대가 탈퇴한 회원인 경우 예외가 발생한다.")
        @Test
        void reverseFriendLiked_shouldThrownWhenTargetIsBlind() {
            // given
            Member targetMember = createMember(TARGET_EMAIL, TARGET_GAMENAME);

            // 대상 회원을 탈퇴 처리
            targetMember.updateBlind(true);
            memberRepository.save(targetMember);

            // when // then
            assertThatThrownBy(() -> friendFacadeService.reverseFriendLiked(member, targetMember.getId()))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(ErrorCode.TARGET_MEMBER_DEACTIVATED.getMessage());
        }

        @DisplayName("친구 즐겨찾기 설정/해제 실패: 상대가 친구가 아닌 경우 예외가 발생한다.")
        @Test
        void reverseFriendLiked_shouldThrownWhenTargetIsNotFriend() {
            // given
            Member targetMember = createMember(TARGET_EMAIL, TARGET_GAMENAME);

            // when // then
            assertThatThrownBy(() -> friendFacadeService.reverseFriendLiked(member, targetMember.getId()))
                    .isInstanceOf(FriendException.class)
                    .hasMessage(ErrorCode.MEMBERS_NOT_FRIEND.getMessage());
        }

    }

    @Nested
    @DisplayName("친구 삭제")
    class DeleteFriendTest {

        @DisplayName("친구 삭제 성공")
        @Test
        void deleteFriendSucceeds() {
            // given
            Member targetMember = createMember(TARGET_EMAIL, TARGET_GAMENAME);

            // 두 회원 간 친구 관계 생성
            friendRepository.save(Friend.create(member, targetMember));
            friendRepository.save(Friend.create(targetMember, member));

            // when
            friendFacadeService.deleteFriend(member, targetMember.getId());

            // then
            assertFalse(friendRepository.existsByFromMemberAndToMember(member, targetMember));
            assertFalse(friendRepository.existsByFromMemberAndToMember(targetMember, member));
        }

        @DisplayName("친구 삭제 실패: 본인 id를 요청한 경우 예외가 발생한다.")
        @Test
        void deleteFriend_shouldThrowWhenTargetIsSelf() {
            // when // then
            assertThatThrownBy(() -> friendFacadeService.deleteFriend(member, member.getId()))
                    .isInstanceOf(FriendException.class)
                    .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
        }

        @DisplayName("친구 삭제 실패: 두 회원이 친구 관계가 아닌 경우 예외가 발생한다.")
        @Test
        void deleteFriend_shouldThrowWhenNotFriend() {
            // given
            Member targetMember = createMember(TARGET_EMAIL, TARGET_GAMENAME);

            // when // then
            assertThatThrownBy(() -> friendFacadeService.deleteFriend(member, targetMember.getId()))
                    .isInstanceOf(FriendException.class)
                    .hasMessage(ErrorCode.MEMBERS_NOT_FRIEND.getMessage());
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
