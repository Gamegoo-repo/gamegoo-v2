package com.gamegoo.gamegoo_v2.chat.service;

import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.member.domain.Member;
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

}
