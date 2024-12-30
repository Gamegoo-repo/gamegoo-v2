package com.gamegoo.gamegoo_v2.chat.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.core.common.validator.ChatValidator;
import com.gamegoo.gamegoo_v2.core.exception.ChatException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final ChatroomRepository chatroomRepository;
    private final ChatRepository chatRepository;
    private final MemberChatroomRepository memberChatroomRepository;
    private final ChatValidator chatValidator;

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
     * @return
     */
    public Slice<Chat> getRecentChatSlice(Member member, Chatroom chatroom) {
        chatValidator.validateMemberChatroom(member.getId(), chatroom.getId());
        return chatRepository.findRecentChats(chatroom.getId(), member.getId(), PAGE_SIZE);
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

    /**
     * 모든 chatroom의 상대 회원을 반환하는 메소드
     *
     * @param member
     * @param chatroomIds
     * @return
     */
    public Map<Long, Member> getChatroomTargetMembersBatch(Member member, List<Long> chatroomIds) {
        return memberChatroomRepository.findTargetMembersBatch(chatroomIds, member.getId());
    }

    /**
     * 해당 chatroom의 메시지 내역 slice 객체를 반환하는 메소드
     *
     * @param member
     * @param chatroom
     * @param cursor
     * @return
     */
    public Slice<Chat> getChatSliceByCursor(Member member, Chatroom chatroom, Long cursor) {
        chatValidator.validateMemberChatroom(member.getId(), chatroom.getId());
        return chatRepository.findChatsByCursor(cursor, chatroom.getId(), member.getId(), PAGE_SIZE);
    }

    /**
     * 회원이 입장한 상태인 모든 chatroom list 반환하는 메소드
     *
     * @param member
     * @return
     */
    public List<Chatroom> getActiveChatrooms(Member member) {
        return chatroomRepository.findActiveChatrooms(member.getId());
    }

    /**
     * 회원이 입장한 상태인 모든 memberChatroom list 반환하는 메소드
     *
     * @param member
     * @return
     */
    public List<MemberChatroom> getActiveMemberChatrooms(Member member) {
        return memberChatroomRepository.findAllActiveMemberChatroomByMemberId(member.getId());
    }

    /**
     * 해당 채팅방의 안읽은 메시지 개수를 반환하는 메소드
     *
     * @param member
     * @param chatroom
     * @return
     */
    public int countUnreadChats(Member member, Chatroom chatroom) {
        chatValidator.validateMemberChatroom(member.getId(), chatroom.getId());
        return chatRepository.countUnreadChats(member.getId(), chatroom.getId());
    }

    /**
     * 모든 채팅방의 안읽은 메시지 개수를 반환하는 메소드
     *
     * @param member
     * @param chatroomIds
     * @return
     */
    public Map<Long, Integer> countUnreadChatsBatch(Member member, List<Long> chatroomIds) {
        return chatRepository.countUnreadChatsBatch(chatroomIds, member.getId());
    }

    /**
     * 해당 채팅방에 해당 timestamp를 갖는 chat 엔티티 조회 메소드
     *
     * @param chatroom
     * @param timestamp
     * @return
     */
    public Chat getChatByChatroomAndTimestamp(Chatroom chatroom, Long timestamp) {
        return chatRepository.findByChatroomAndTimestamp(chatroom, timestamp).orElseThrow(
                () -> new ChatException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));
    }

    /**
     * id로 chat 엔티티 배치 조회 메소드
     *
     * @param chatIds
     * @return
     */
    public Map<Long, Chat> findAllChatsBatch(List<Long> chatIds) {
        List<Chat> chats = chatRepository.findAllById(chatIds);

        Map<Long, Chat> chatMap = new HashMap<>();
        for (Chat chat : chats) {
            chatMap.put(chat.getChatroom().getId(), chat);
        }

        return chatMap;
    }

}
