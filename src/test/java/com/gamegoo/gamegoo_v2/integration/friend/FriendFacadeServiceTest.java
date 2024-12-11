package com.gamegoo.gamegoo_v2.integration.friend;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequest;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequestStatus;
import com.gamegoo.gamegoo_v2.friend.dto.FriendRequestResponse;
import com.gamegoo.gamegoo_v2.friend.dto.StarFriendResponse;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRequestRepository;
import com.gamegoo.gamegoo_v2.friend.service.FriendFacadeService;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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
class FriendFacadeServiceTest {

    @Autowired
    FriendFacadeService friendFacadeService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BlockRepository blockRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    private static final String MEMBER_EMAIL = "test@gmail.com";
    private static final String MEMBER_GAMENAME = "member";

    @AfterEach
    void tearDown() {
        friendRepository.deleteAllInBatch();
        friendRequestRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("친구 요청 전송 성공")
    @Test
    void sendFriendRequestSucceeds() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when
        FriendRequestResponse response = friendFacadeService.sendFriendRequest(member,
                targetMember.getId());

        // then
        assertThat(response.getTargetMemberId()).isEqualTo(targetMember.getId());
        assertThat(response.getMessage()).isEqualTo("친구 요청 전송 성공");
    }

    @DisplayName("친구 요청 전송 실패: 본인 id를 요청한 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenTargetIsSelf() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

        System.out.println("member.getId() = " + member.getId());

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, member.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
    }

    @DisplayName("친구 요청 전송 실패: 상대가 탈퇴한 회원인 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenTargetIsBlind() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 대상 회원을 탈퇴 처리
        targetMember.updateBlind(true);
        memberRepository.save(targetMember);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, targetMember.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(ErrorCode.TARGET_MEMBER_DEACTIVATED.getMessage());
    }

    @DisplayName("친구 요청 전송 실패: 내가 상대를 차단한 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenTargetIsBlocked() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 내가 상대를 차단 처리
        blockRepository.save(Block.create(member, targetMember));

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_TARGET_IS_BLOCKED.getMessage());
    }

    @DisplayName("친구 요청 전송 실패: 상대가 나를 차단한 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenBlockedByTarget() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 상대가 나를 차단 처리
        blockRepository.save(Block.create(targetMember, member));

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.BLOCKED_BY_FRIEND_TARGET.getMessage());
    }

    @DisplayName("친구 요청 전송 실패: 두 회원이 이미 친구 관계인 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenAlreadyFriend() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 두 회원 간 친구 관계 생성
        friendRepository.save(Friend.create(member, targetMember));
        friendRepository.save(Friend.create(targetMember, member));

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.ALREADY_FRIEND.getMessage());
    }

    @DisplayName("친구 요청 전송 실패: 내가 보낸 수락 대기 중인 친구 요청이 이미 존재하는 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenPendingRequestToTargetExists() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 나 -> 상대 친구 요청 생성
        friendRequestRepository.save(FriendRequest.create(member, targetMember));

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.MY_PENDING_FRIEND_REQUEST_EXIST.getMessage());
    }

    @DisplayName("친구 요청 전송 실패: 상대가 보낸 수락 대기 중인 친구 요청이 이미 존재하는 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenPendingRequestToMeExists() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 상대 -> 나 친구 요청 생성
        friendRequestRepository.save(FriendRequest.create(targetMember, member));

        // when // then
        assertThatThrownBy(() -> friendFacadeService.sendFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.TARGET_PENDING_FRIEND_REQUEST_EXIST.getMessage());
    }

    @DisplayName("친구 요청 수락 성공")
    @Test
    void acceptFriendRequestSucceeds() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 상대 -> 나 친구 요청 생성
        friendRequestRepository.save(FriendRequest.create(targetMember, member));

        // when
        FriendRequestResponse response = friendFacadeService.acceptFriendRequest(member, targetMember.getId());

        // then
        assertThat(response.getTargetMemberId()).isEqualTo(targetMember.getId());
        assertTrue(friendRepository.existsByFromMemberAndToMember(member, targetMember));
    }

    @DisplayName("친구 요청 수락 실패: 본인 id를 요청한 경우 예외가 발생한다.")
    @Test
    void acceptFriendRequest_shouldThrowWhenTargetIsSelf() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.acceptFriendRequest(member, member.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
    }

    @DisplayName("친구 요청 수락 실패: PENDING 상태인 친구 요청이 없는 경우 예외가 발생한다")
    @Test
    void acceptFriendRequest_shouldThrowWhenNoPendingRequest() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when // then
        assertThatThrownBy(() -> friendFacadeService.acceptFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST.getMessage());
    }

    @DisplayName("친구 요청 거절 성공")
    @Test
    void rejectFriendRequestSucceeds() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 상대 -> 나 친구 요청 생성
        friendRequestRepository.save(FriendRequest.create(targetMember, member));

        // when
        FriendRequestResponse response = friendFacadeService.rejectFriendRequest(member, targetMember.getId());

        // then
        assertThat(response.getTargetMemberId()).isEqualTo(targetMember.getId());
        assertFalse(friendRepository.existsByFromMemberAndToMember(member, targetMember));
    }

    @DisplayName("친구 요청 거절 실패: 본인 id를 요청한 경우 예외가 발생한다.")
    @Test
    void rejectFriendRequest_shouldThrowWhenTargetIsSelf() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.rejectFriendRequest(member, member.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
    }

    @DisplayName("친구 요청 거절 실패: PENDING 상태인 친구 요청이 없는 경우 예외가 발생한다")
    @Test
    void rejectFriendRequest_shouldThrowWhenNoPendingRequest() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when // then
        assertThatThrownBy(() -> friendFacadeService.rejectFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST.getMessage());
    }
  
    @DisplayName("친구 요청 취소 성공")
    @Test
    void cancelFriendRequestSucceeds() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");
      
        // 나 -> 상대 친구 요청 생성
        friendRequestRepository.save(FriendRequest.create(member, targetMember));

        // when
        FriendRequestResponse response = friendFacadeService.cancelFriendRequest(member, targetMember.getId());

        // then
        assertThat(response.getTargetMemberId()).isEqualTo(targetMember.getId());
        assertThat(friendRequestRepository.findByFromMemberAndToMemberAndStatus(member, targetMember,
                FriendRequestStatus.CANCELLED)).isNotEmpty();
    }

    @DisplayName("친구 요청 취소 실패: 본인 id를 요청한 경우 예외가 발생한다.")
    @Test
    void cancelFriendRequest_shouldThrowWhenTargetIsSelf() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.cancelFriendRequest(member, member.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
    }
  
    @DisplayName("친구 요청 취소 실패: PENDING 상태인 친구 요청이 없는 경우 예외가 발생한다")
    @Test
    void cancelFriendRequest_shouldThrowWhenNoPendingRequest() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when // then
        assertThatThrownBy(() -> friendFacadeService.cancelFriendRequest(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST.getMessage());
    }

    @DisplayName("친구 즐겨찾기 설정 성공: 즐겨찾기 되지 않은 친구를 요청한 경우 즐겨찾기가 설정된다.")
    @Test
    void reverseFriendLikedSucceedsWhenNotLiked() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

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
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 두 회원 간 친구 관계 생성
        Friend savedFriend = friendRepository.save(Friend.create(member, targetMember));
        friendRepository.save(Friend.create(targetMember, member));

        // member -> targetMember 즐겨찾기 설정
        savedFriend.reverseLiked();

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
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.reverseFriendLiked(member, member.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
    }

    @DisplayName("친구 즐겨찾기 설정/해제 실패: 상대가 탈퇴한 회원인 경우 예외가 발생한다.")
    @Test
    void reverseFriendLiked_shouldThrownWhenTargetIsBlind() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // 대상 회원을 탈퇴 처리
        targetMember.updateBlind(true);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.reverseFriendLiked(member, targetMember.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(ErrorCode.TARGET_MEMBER_DEACTIVATED.getMessage());
    }

    @DisplayName("친구 즐겨찾기 설정/해제 실패: 상대가 친구가 아닌 경우 예외가 발생한다.")
    @Test
    void reverseFriendLiked_shouldThrownWhenTargetIsNotFriend() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when // then
        assertThatThrownBy(() -> friendFacadeService.reverseFriendLiked(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.MEMBERS_NOT_FRIEND.getMessage());
    }

    @DisplayName("친구 삭제 성공")
    @Test
    void deleteFriendSucceeds() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

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
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

        // when // then
        assertThatThrownBy(() -> friendFacadeService.deleteFriend(member, member.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.FRIEND_BAD_REQUEST.getMessage());
    }

    @DisplayName("친구 삭제 실패: 두 회원이 친구 관계가 아닌 경우 예외가 발생한다.")
    @Test
    void deleteFriend_shouldThrowWhenNotFriend() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when // then
        assertThatThrownBy(() -> friendFacadeService.deleteFriend(member, targetMember.getId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(ErrorCode.MEMBERS_NOT_FRIEND.getMessage());
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
