package com.gamegoo.gamegoo_v2.integration.friend;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequest;
import com.gamegoo.gamegoo_v2.friend.dto.SendFriendRequestResponse;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRequestRepository;
import com.gamegoo.gamegoo_v2.friend.service.FriendFacadeService;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class FriendFacadeServiceTest {

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

    @DisplayName("친구 요청 전송 성공")
    @Test
    void sendFriendRequestSucceeds() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);
        Member targetMember = createMember("target@naver.com", "target");

        // when
        SendFriendRequestResponse sendFriendRequestResponse = friendFacadeService.sendFriendRequest(member,
                targetMember.getId());

        // then
        assertThat(sendFriendRequestResponse.getTargetMemberId()).isEqualTo(targetMember.getId());
        assertThat(sendFriendRequestResponse.getMessage()).isEqualTo("친구 요청 전송 성공");
    }

    @DisplayName("친구 요청 전송 실패: 본인 id를 요청한 경우 예외가 발생한다.")
    @Test
    void sendFriendRequest_shouldThrowWhenTargetIsSelf() {
        // given
        Member member = createMember(MEMBER_EMAIL, MEMBER_GAMENAME);

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
