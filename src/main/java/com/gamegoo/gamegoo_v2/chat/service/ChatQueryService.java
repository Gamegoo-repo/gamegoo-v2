package com.gamegoo.gamegoo_v2.chat.service;

import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.core.exception.ChatException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final ChatroomRepository chatroomRepository;
    private final ChatRepository chatRepository;

    private static final int PAGE_SIZE = 20;
    private final MemberChatroomRepository memberChatroomRepository;

    /**
     * 두 회원 사이에 존재하는 chatroom을 반환하는 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    public Optional<Chatroom> findExistingChatroom(Member member, Member targetMember) {
        return chatroomRepository.findChatroomByMemberIds(member.getId(), targetMember.getId());
    }

    /**
     * 최근 메시지 내역 slice 객체를 반환하는 메소드
     *
     * @param member
     * @param chatroom
     * @param memberChatroom
     * @return
     */
    public Slice<Chat> getRecentChatSlice(Member member, Chatroom chatroom, MemberChatroom memberChatroom) {
        return chatRepository.findRecentChats(chatroom.getId(), memberChatroom.getId(), member.getId(), PAGE_SIZE);
    }

    /**
     * uuid에 해당하는 chatroom을 반환하는 메소드
     *
     * @param uuid
     * @return
     */
    public Chatroom getChatroomByUuid(String uuid) {
        return chatroomRepository.findByUuid(uuid).orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    /**
     * 해당 chatrooom의 상대 회원을 반환하는 메소드
     *
     * @param member
     * @param chatroom
     * @return
     */
    public Member getChatroomTargetMember(Member member, Chatroom chatroom) {
        return memberChatroomRepository.findTargetMemberByChatroomIdAndMemberId(chatroom.getId(), member.getId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
    }

}
