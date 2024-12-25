package com.gamegoo.gamegoo_v2.core.common.validator;

import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.core.exception.ChatException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.core.exception.common.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatValidator extends BaseValidator {

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

    /**
     * 해당 채팅방을 나간 상태인 경우 입력받은 Exception을 발생시키는 메소드
     *
     * @param memberChatroom
     * @param exceptionClass
     * @param errorCode
     * @param <T>
     */
    public <T extends GlobalException> void throwIfExited(MemberChatroom memberChatroom, Class<T> exceptionClass,
                                                          ErrorCode errorCode) {
        if (memberChatroom.exited()) {
            throw createException(exceptionClass, errorCode);
        }
    }

}
