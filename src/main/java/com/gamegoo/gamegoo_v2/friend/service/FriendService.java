package com.gamegoo.gamegoo_v2.friend.service;

import com.gamegoo.gamegoo_v2.common.validator.BlockValidator;
import com.gamegoo.gamegoo_v2.common.validator.FriendValidator;
import com.gamegoo.gamegoo_v2.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.exception.FriendException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequest;
import com.gamegoo.gamegoo_v2.friend.repository.FriendRequestRepository;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final BlockValidator blockValidator;
    private final MemberValidator memberValidator;
    private final FriendValidator friendValidator;

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

        return friendRequest;
    }

    private void validateNotSelf(Member member, Member targetMember) {
        if (member.equals(targetMember)) {
            throw new FriendException(ErrorCode.FRIEND_BAD_REQUEST);
        }
    }

    private void validateBlockStatus(Member member, Member targetMember) {
        blockValidator.validateIfBlocked(member, targetMember, FriendException.class,
                ErrorCode.FRIEND_TARGET_IS_BLOCKED);
        blockValidator.validateIfBlocked(targetMember, member, FriendException.class,
                ErrorCode.BLOCKED_BY_FRIEND_TARGET);
    }

}
