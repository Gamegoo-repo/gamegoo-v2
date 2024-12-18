package com.gamegoo.gamegoo_v2.friend.service;

import com.gamegoo.gamegoo_v2.common.validator.BlockValidator;
import com.gamegoo.gamegoo_v2.common.validator.FriendValidator;
import com.gamegoo.gamegoo_v2.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.event.AcceptFriendRequestEvent;
import com.gamegoo.gamegoo_v2.event.RejectFriendRequestEvent;
import com.gamegoo.gamegoo_v2.event.SendFriendRequestEvent;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequest;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequestStatus;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRequestRepository;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final BlockValidator blockValidator;
    private final MemberValidator memberValidator;
    private final FriendValidator friendValidator;
    private final ApplicationEventPublisher eventPublisher;

    private final static int PAGE_SIZE = 10;

    /**
     * 친구 요청 생성 메소드
     *
     * @param member
     * @param targetMember
     */
    @Transactional
    public FriendRequest sendFriendRequest(Member member, Member targetMember) {
        // targetMember로 나 자신을 요청한 경우 검증
        validateNotSelf(member, targetMember);

        // 상대방의 탈퇴 여부 검증
        memberValidator.validateTargetMemberIsNotBlind(targetMember);

        // 두 회원의 차단 여부 검증
        validateBlockStatus(member, targetMember);

        // 두 회원이 이미 친구 관계인 경우 검증
        friendValidator.validateIsNotFriend(member, targetMember);

        // 두 회원 사이 수락 대기중인 친구 요청 존재 여부 검증
        friendValidator.validateNoPendingRequest(member, targetMember);

        // 친구 요청 엔티티 생성 및 저장
        FriendRequest friendRequest = friendRequestRepository.save(FriendRequest.create(member, targetMember));

        // 친구 요청 알림 생성
        eventPublisher.publishEvent(new SendFriendRequestEvent(member.getId(), targetMember.getId()));

        return friendRequest;
    }

    /**
     * targetMember가 보낸 친구 요청 수락 처리 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    @Transactional
    public FriendRequest acceptFriendRequest(Member member, Member targetMember) {
        // targetMember로 나 자신을 요청한 경우 검증
        validateNotSelf(member, targetMember);

        // 수락 대기 상태인 FriendRequest 엔티티 조회 및 검증
        FriendRequest friendRequest = friendRequestRepository.findByFromMemberAndToMemberAndStatus(targetMember,
                        member, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendException(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST));

        // FriendRequest 엔티티 상태 변경
        friendRequest.updateStatus(FriendRequestStatus.ACCEPTED);

        // friend 엔티티 생성 및 저장
        friendRepository.save(Friend.create(member, targetMember));
        friendRepository.save(Friend.create(targetMember, member));

        // targetMember에게 친구 요청 수락 알림 생성
        eventPublisher.publishEvent(new AcceptFriendRequestEvent(member.getId(), targetMember.getId()));

        return friendRequest;
    }

    /**
     * targetMember가 보낸 친구 요청 거절 처리 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    @Transactional
    public FriendRequest rejectFriendRequest(Member member, Member targetMember) {
        // targetMember로 나 자신을 요청한 경우 검증
        validateNotSelf(member, targetMember);

        // 수락 대기 상태인 FriendRequest 엔티티 조회 및 검증
        FriendRequest friendRequest = friendRequestRepository.findByFromMemberAndToMemberAndStatus(targetMember,
                        member, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendException(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST));

        // FriendRequest 엔티티 상태 변경
        friendRequest.updateStatus(FriendRequestStatus.REJECTED);

        // targetMember에게 친구 요청 거절 알림 생성
        eventPublisher.publishEvent(new RejectFriendRequestEvent(member.getId(), targetMember.getId()));

        return friendRequest;
    }

    /**
     * targetMember에게 보낸 친구 요청 취소 처리 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    @Transactional
    public FriendRequest cancelFriendRequest(Member member, Member targetMember) {
        // targetMember로 나 자신을 요청한 경우 검증
        validateNotSelf(member, targetMember);

        // 수락 대기 상태인 FriendRequest 엔티티 조회 및 검증
        FriendRequest friendRequest = friendRequestRepository.findByFromMemberAndToMemberAndStatus(member,
                        targetMember, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendException(ErrorCode.PENDING_FRIEND_REQUEST_NOT_EXIST));

        // FriendRequest 엔티티 상태 변경
        friendRequest.updateStatus(FriendRequestStatus.CANCELLED);

        return friendRequest;
    }


    /**
     * targetMember를 즐겨찾기 설정 또는 해제 처리 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    @Transactional
    public Friend reverseFriendLiked(Member member, Member targetMember) {
        // targetMember로 나 자신을 요청한 경우 검증
        validateNotSelf(member, targetMember);

        // targetMember의 탈퇴 여부 검증
        memberValidator.validateTargetMemberIsNotBlind(targetMember);

        // 두 회원이 친구 관계가 맞는지 검증
        friendValidator.validateIsFriend(member, targetMember);

        // liked 상태 변경
        Friend friend = friendRepository.findByFromMemberAndToMember(member, targetMember);
        friend.reverseLiked();

        return friend;
    }

    /**
     * 두 회원 사이 친구 관계 삭제 메소드
     *
     * @param member
     * @param targetMember
     */
    @Transactional
    public void deleteFriend(Member member, Member targetMember) {
        // targetMember로 나 자신을 요청한 경우 검증
        validateNotSelf(member, targetMember);

        // 두 회원이 친구 관계인지 검증
        Friend friend1 = friendRepository.findByFromMemberAndToMember(member, targetMember);
        Friend friend2 = friendRepository.findByFromMemberAndToMember(targetMember, member);

        if (friend1 == null && friend2 == null) {
            throw new FriendException(ErrorCode.MEMBERS_NOT_FRIEND);
        }

        // 친구 관계 삭제
        if (friend1 != null) {
            friendRepository.delete(friend1);
        }
        if (friend2 != null) {
            friendRepository.delete(friend2);
        }
    }

    /**
     * 해당 회원의 친구 목록 Slice 객체 반환하는 메소드
     *
     * @param member
     * @return
     */
    public Slice<Friend> getFriends(Member member, Long cursor) {
        return friendRepository.findFriendsByCursorAndOrdered(member.getId(), cursor, PAGE_SIZE);
    }

    /**
     * 소환사명으로 친구 목록 조회하는 메소드
     *
     * @param member
     * @param query
     * @return
     */
    public List<Friend> searchFriendByGamename(Member member, String query) {
        validateSearchQuery(query);
        return friendRepository.findFriendsByQueryStringAndOrdered(member.getId(), query);
    }

    private void validateNotSelf(Member member, Member targetMember) {
        if (member.getId().equals(targetMember.getId())) {
            throw new FriendException(ErrorCode.FRIEND_BAD_REQUEST);
        }
    }

    private void validateBlockStatus(Member member, Member targetMember) {
        blockValidator.validateIfBlocked(member, targetMember, FriendException.class,
                ErrorCode.FRIEND_TARGET_IS_BLOCKED);
        blockValidator.validateIfBlocked(targetMember, member, FriendException.class,
                ErrorCode.BLOCKED_BY_FRIEND_TARGET);
    }

    private void validateSearchQuery(String query) {
        if (query.length() > 100) {
            throw new FriendException(ErrorCode.FRIEND_SEARCH_QUERY_BAD_REQUEST);
        }
    }

}
