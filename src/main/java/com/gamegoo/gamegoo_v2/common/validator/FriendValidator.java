package com.gamegoo.gamegoo_v2.common.validator;

import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequestStatus;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRepository;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRequestRepository;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendValidator {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;

    /**
     * 두 회원이 서로 친구이면 예외 발생
     *
     * @param member
     * @param targetMember
     */
    public void validateIsNotFriend(Member member, Member targetMember) {
        if (friendRepository.existsByFromMemberAndToMember(member, targetMember)) {
            throw new FriendException(ErrorCode.ALREADY_FRIEND);
        }
    }

    /**
     * 두 회원이 서로 친구가 아니면 예외 발생
     *
     * @param member
     * @param targetMember
     */
    public void validateIsFriend(Member member, Member targetMember) {
        boolean exists1 = friendRepository.existsByFromMemberAndToMember(member, targetMember);
        boolean exists2 = friendRepository.existsByFromMemberAndToMember(targetMember, member);

        if (!exists1 || !exists2) {
            throw new FriendException(ErrorCode.MEMBERS_NOT_FRIEND);
        }
    }

    /**
     * 두 회원 사이에 PENDING 상태인 친구 요청이 존재하면 예외 발생
     *
     * @param member
     * @param targetMember
     */
    public void validateNoPendingRequest(Member member, Member targetMember) {
        boolean myPendingRequestExists = friendRequestRepository.existsByFromMemberAndToMemberAndStatus(
                member, targetMember, FriendRequestStatus.PENDING);

        if (myPendingRequestExists) {
            throw new FriendException(ErrorCode.MY_PENDING_FRIEND_REQUEST_EXIST);
        }

        boolean targetPendingRequestExists = friendRequestRepository.existsByFromMemberAndToMemberAndStatus(
                targetMember, member, FriendRequestStatus.PENDING);

        if (targetPendingRequestExists) {
            throw new FriendException(ErrorCode.TARGET_PENDING_FRIEND_REQUEST_EXIST);
        }
    }

}
