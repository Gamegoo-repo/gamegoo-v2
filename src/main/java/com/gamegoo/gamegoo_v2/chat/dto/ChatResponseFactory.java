package com.gamegoo.gamegoo_v2.chat.dto;

import com.gamegoo.gamegoo_v2.block.service.BlockService;
import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatMessageListResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatMessageResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.SystemFlagResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.SystemMessageResponse;
import com.gamegoo.gamegoo_v2.friend.service.FriendService;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatResponseFactory {

    private final FriendService friendService;
    private final BlockService blockService;

    public ChatMessageListResponse toChatMessageListResponse(Slice<Chat> chatSlice) {
        List<ChatMessageResponse> chatMessageResponseList = chatSlice.stream()
                .map(chat -> {
                    if (chat.getSystemType() == null) {
                        return SystemMessageResponse.of(chat);
                    }
                    return ChatMessageResponse.of(chat);
                })
                .toList();

        Long nextCursor = chatSlice.hasNext()
                ? chatSlice.getContent().get(0).getTimestamp()
                : null;

        return ChatMessageListResponse.builder()
                .chatMessageList(chatMessageResponseList)
                .listSize(chatMessageResponseList.size())
                .hasNext(chatSlice.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

    public ChatMessageListResponse toChatMessageListResponse() {
        return ChatMessageListResponse.builder()
                .chatMessageList(new ArrayList<>())
                .listSize(0)
                .hasNext(false)
                .nextCursor(null)
                .build();
    }

    public EnterChatroomResponse toEnterChatroomResponse(Member member, Member targetMember, String chatroomUuid,
            SystemFlagResponse system, ChatMessageListResponse chatMessageListResponse) {
        String gameName = targetMember.isBlind()
                ? "(탈퇴한 사용자)"
                : targetMember.getGameName();

        return EnterChatroomResponse.builder()
                .uuid(chatroomUuid)
                .memberId(targetMember.getId())
                .gameName(gameName)
                .memberProfileImg(targetMember.getProfileImage())
                .friend(friendService.isFriend(member, targetMember))
                .blocked(blockService.isBlocked(member, targetMember))
                .blind(targetMember.isBlind())
                .friendRequestMemberId(friendService.getFriendRequestMemberId(member, targetMember))
                .system(system)
                .chatMessageListResponse(chatMessageListResponse)
                .build();
    }

}
