package com.gamegoo.gamegoo_v2.core.common.validator;

import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.core.exception.ChatException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatValidator {

    private final MemberChatroomRepository memberChatroomRepository;

    /**
     * 해당 chatroom이 member의 것이 맞는지 검증
     *
     * @param memberId
     * @param ChatroomId
     */
    public MemberChatroom validateMemberChatroom(Long memberId, Long ChatroomId) {
        return memberChatroomRepository.findByMemberIdAndChatroomId(memberId, ChatroomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_ACCESS_DENIED));
    }

}
