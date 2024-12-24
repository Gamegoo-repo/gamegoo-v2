package com.gamegoo.gamegoo_v2.social.friend.service;

import com.gamegoo.gamegoo_v2.social.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.social.friend.domain.FriendRequest;
import com.gamegoo.gamegoo_v2.social.friend.dto.DeleteFriendResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.FriendInfoResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.FriendListResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.FriendRequestResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.StarFriendResponse;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendFacadeService {

    private final FriendService friendService;
    private final MemberService memberService;

    /**
     * 친구 요청 전송 Facade 메소드
     *
     * @param member
     * @param targetMemberId
     * @return
     */
    @Transactional
    public FriendRequestResponse sendFriendRequest(Member member, Long targetMemberId) {
        Member targetMember = memberService.findMember(targetMemberId);
        FriendRequest friendRequest = friendService.sendFriendRequest(member, targetMember);

        return FriendRequestResponse.of(friendRequest.getToMember().getId(), "친구 요청 전송 성공");
    }

    /**
     * 친구 요청 수락 Facade 메소드
     *
     * @param member
     * @param targetMemberId
     * @return
     */
    @Transactional
    public FriendRequestResponse acceptFriendRequest(Member member, Long targetMemberId) {
        Member targetMember = memberService.findMember(targetMemberId);
        FriendRequest friendRequest = friendService.acceptFriendRequest(member, targetMember);

        return FriendRequestResponse.of(friendRequest.getFromMember().getId(), "친구 요청 수락 성공");
    }

    /**
     * 친구 요청 거절 Facade 메소드
     *
     * @param member
     * @param targetMemberId
     * @return
     */
    @Transactional
    public FriendRequestResponse rejectFriendRequest(Member member, Long targetMemberId) {
        Member targetMember = memberService.findMember(targetMemberId);
        FriendRequest friendRequest = friendService.rejectFriendRequest(member, targetMember);

        return FriendRequestResponse.of(friendRequest.getFromMember().getId(), "친구 요청 거절 성공");
    }

    /**
     * 친구 요청 취소 Facade 메소드
     *
     * @param member
     * @param targetMemberId
     * @return
     */
    @Transactional
    public FriendRequestResponse cancelFriendRequest(Member member, Long targetMemberId) {
        Member targetMember = memberService.findMember(targetMemberId);
        FriendRequest friendRequest = friendService.cancelFriendRequest(member, targetMember);

        return FriendRequestResponse.of(friendRequest.getToMember().getId(), "친구 요청 취소 성공");
    }

    /**
     * 친구 즐겨찾기 설정/해제 Facade 메소드
     *
     * @param member
     * @param friendMemberId
     * @return
     */
    @Transactional
    public StarFriendResponse reverseFriendLiked(Member member, Long friendMemberId) {
        Member friendMember = memberService.findMember(friendMemberId);
        Friend friend = friendService.reverseFriendLiked(member, friendMember);

        return StarFriendResponse.of(friend);
    }

    /**
     * 친구 삭제 Facade 메소드
     *
     * @param member
     * @param targetMemberId
     * @return
     */
    @Transactional
    public DeleteFriendResponse deleteFriend(Member member, Long targetMemberId) {
        Member targetMember = memberService.findMember(targetMemberId);
        friendService.deleteFriend(member, targetMember);

        return DeleteFriendResponse.of(targetMemberId);
    }

    /**
     * 모든 친구 id 목록 조회 Facade 메소드
     *
     * @param member
     * @return
     */
    public List<Long> getFriendIdList(Member member) {
        return friendService.getFriendIdList(member);
    }

    /**
     * 친구 목록 조회 Facade 메소드
     *
     * @param member
     * @param cursor
     * @return
     */
    public FriendListResponse getFriends(Member member, Long cursor) {
        return FriendListResponse.of(friendService.getFriendSlice(member, cursor));
    }

    /**
     * 소환사명으로 친구 조회 Facade 메소드
     *
     * @param member
     * @param query
     * @return
     */
    public List<FriendInfoResponse> searchFriend(Member member, String query) {
        return friendService.searchFriendByGamename(member, query).stream()
                .map(FriendInfoResponse::of)
                .toList();
    }

}