package com.gamegoo.gamegoo_v2.chat.service;

import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.dto.ChatResponseFactory;
import com.gamegoo.gamegoo_v2.chat.dto.response.ChatMessageListResponse;
import com.gamegoo.gamegoo_v2.chat.dto.response.EnterChatroomResponse;
import com.gamegoo.gamegoo_v2.common.validator.BlockValidator;
import com.gamegoo.gamegoo_v2.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gamegoo.gamegoo_v2.exception.common.ErrorCode.CHAT_START_FAILED_BLOCKED_BY_CHAT_TARGET;
import static com.gamegoo.gamegoo_v2.exception.common.ErrorCode.CHAT_START_FAILED_CHAT_TARGET_IS_BLOCKED;
import static com.gamegoo.gamegoo_v2.exception.common.ErrorCode.CHAT_START_FAILED_TARGET_USER_DEACTIVATED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatFacadeService {

    private final MemberService memberService;
    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    private final MemberValidator memberValidator;
    private final BlockValidator blockValidator;

    private final ChatResponseFactory chatResponseFactory;

    /**
     * 대상 회원과 채팅 시작 Facade 메소드
     * 기존에 채팅방이 있는 경우 채팅방 입장 처리, 기존에 채팅방이 없는 경우 새로운 채팅방 생성
     *
     * @param member
     * @param targetMemberId
     * @return
     */
    @Transactional
    public EnterChatroomResponse startChatroomByMemberId(Member member, Long targetMemberId) {
        // 대상 회원 검증
        Member targetMember = memberService.findMember(targetMemberId);
        memberValidator.validateDifferentMembers(member, targetMember);

        // 내가 상대 회원을 차단하지 않았는지 검증
        blockValidator.throwIfBlocked(member, targetMember, ChatException.class,
                CHAT_START_FAILED_CHAT_TARGET_IS_BLOCKED);

        Chatroom chatroom;
        ChatMessageListResponse chatMessageListResponse;

        Optional<Chatroom> existingChatroom = chatQueryService.findExistingChatroom(member, targetMember);

        if (existingChatroom.isPresent()) { // 기존 채팅방이 존재하는 경우
            chatroom = existingChatroom.get();

            // 기존 채팅방에 입장 처리
            MemberChatroom memberChatroom = chatCommandService.enterExistingChatroom(member, targetMember,
                    existingChatroom.get());

            // 최근 메시지 내역 조회
            Slice<Chat> chatSlice = chatQueryService.getRecentChatSlice(member, chatroom, memberChatroom);

            // 응답 dto 생성
            chatMessageListResponse = chatResponseFactory.toChatMessageListResponse(chatSlice);
        } else {// 기존에 채팅방이 존재하지 않는 경우
            // 상대가 나를 차단하지 않았는지 검증
            blockValidator.throwIfBlocked(targetMember, member, ChatException.class,
                    CHAT_START_FAILED_BLOCKED_BY_CHAT_TARGET);

            // 상대가 탈퇴하지 않았는지 검증
            memberValidator.throwIfBlind(targetMember, ChatException.class, CHAT_START_FAILED_TARGET_USER_DEACTIVATED);

            // 새 채팅방 생성
            chatroom = chatCommandService.createChatroom(member, targetMember);

            // 응답 dto 생성
            chatMessageListResponse = chatResponseFactory.toChatMessageListResponse();
        }

        return chatResponseFactory.toEnterChatroomResponse(member, targetMember, chatroom.getUuid(), null,
                chatMessageListResponse);
    }

}
