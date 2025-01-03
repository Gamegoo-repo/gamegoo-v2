package com.gamegoo.gamegoo_v2.social.friend.controller;

import com.gamegoo.gamegoo_v2.account.auth.annotation.AuthMember;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.core.common.ApiResponse;
import com.gamegoo.gamegoo_v2.core.common.annotation.ValidCursor;
import com.gamegoo.gamegoo_v2.social.friend.dto.DeleteFriendResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.FriendInfoResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.FriendListResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.FriendRequestResponse;
import com.gamegoo.gamegoo_v2.social.friend.dto.StarFriendResponse;
import com.gamegoo.gamegoo_v2.social.friend.service.FriendFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Friend", description = "친구 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/friend")
@Validated
public class FriendController {

    private final FriendFacadeService friendFacadeService;

    @Operation(summary = "친구 요청 전송 API", description = "대상 회원에게 친구 요청을 전송하는 API 입니다. 대상 회원에게 친구 요청 알림을 전송합니다.")
    @Parameter(name = "memberId", description = "친구 요청을 전송할 대상 회원의 id 입니다.")
    @PostMapping("/request/{memberId}")
    public ApiResponse<FriendRequestResponse> sendFriendRequest(
            @PathVariable(name = "memberId") Long targetMemberId, @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.sendFriendRequest(member, targetMemberId));
    }

    @Operation(summary = "친구 요청 수락 API", description = "대상 회원이 보낸 친구 요청을 수락 처리하는 API 입니다.")
    @Parameter(name = "memberId", description = "친구 요청을 수락할 대상 회원의 id 입니다.")
    @PatchMapping("/request/{memberId}/accept")
    public ApiResponse<FriendRequestResponse> acceptFriendRequest(@PathVariable(name = "memberId") Long targetMemberId,
                                                                  @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.acceptFriendRequest(member, targetMemberId));
    }

    @Operation(summary = "친구 요청 거절 API", description = "대상 회원이 보낸 친구 요청을 거절 처리하는 API 입니다.")
    @Parameter(name = "memberId", description = "친구 요청을 거절할 대상 회원의 id 입니다.")
    @PatchMapping("/request/{memberId}/reject")
    public ApiResponse<FriendRequestResponse> rejectFriendRequest(@PathVariable(name = "memberId") Long targetMemberId,
                                                                  @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.rejectFriendRequest(member, targetMemberId));
    }

    @Operation(summary = "친구 요청 취소 API", description = "대상 회원에게 보낸 친구 요청을 취소하는 API 입니다.")
    @Parameter(name = "memberId", description = "친구 요청을 취소할 대상 회원의 id 입니다.")
    @DeleteMapping("/request/{memberId}")
    public ApiResponse<FriendRequestResponse> cancelFriendRequest(@PathVariable(name = "memberId") Long targetMemberId,
                                                                  @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.cancelFriendRequest(member, targetMemberId));
    }

    @Operation(summary = "친구 즐겨찾기 설정/해제 API", description = "대상 친구 회원을 즐겨찾기 설정/해제 하는 API 입니다.")
    @Parameter(name = "memberId", description = "즐겨찾기 설정/해제할 친구 회원의 id 입니다.")
    @PatchMapping("/{memberId}/star")
    public ApiResponse<StarFriendResponse> reverseFriendLiked(@PathVariable(name = "memberId") Long friendMemberId,
                                                              @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.reverseFriendLiked(member, friendMemberId));
    }

    @Operation(summary = "친구 삭제 API", description = "친구 회원과의 친구 관계를 끊는 API 입니다.")
    @Parameter(name = "memberId", description = "삭제 처리할 친구 회원의 id 입니다.")
    @DeleteMapping("/{memberId}")
    public ApiResponse<DeleteFriendResponse> deleteFriend(@PathVariable(name = "memberId") Long targetMemberId,
                                                          @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.deleteFriend(member, targetMemberId));
    }

    @Operation(summary = "친구 목록 조회 API", description = "해당 회원의 친구 목록을 조회하는 API 입니다. 이름 오름차순(한글-영문-숫자 순)으로 정렬해 제공합니다."
            + "cursor를 보내지 않으면 상위 10개 친구 목록을 조회합니다.")
    @Parameter(name = "cursor", description = "페이징을 위한 커서, 이전 친구 목록 조회에서 응답받은 nextCursor를 보내주세요.")
    @GetMapping
    public ApiResponse<FriendListResponse> getFriendList(
            @ValidCursor @RequestParam(name = "cursor", required = false) Long cursor, @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.getFriends(member, cursor));
    }

    @Operation(summary = "소환사명으로 친구 검색 API", description = "해당 회원의 친구 중, query string으로 시작하는 소환사명을 가진 모든 친구 목록을 조회합니다.")
    @Parameter(name = "query", description = "친구 목록 검색을 위한 소환사명 string으로, 100자 이하여야 합니다.")
    @GetMapping("/search")
    public ApiResponse<List<FriendInfoResponse>> searchFriend(@RequestParam(name = "query") String query,
                                                              @AuthMember Member member) {
        return ApiResponse.ok(friendFacadeService.searchFriend(member, query));
    }

}
