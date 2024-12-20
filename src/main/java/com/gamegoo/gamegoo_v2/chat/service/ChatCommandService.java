package com.gamegoo.gamegoo_v2.chat.service;

import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.common.validator.BlockValidator;
import com.gamegoo.gamegoo_v2.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final MemberValidator memberValidator;
    private final BlockValidator blockValidator;

    private final MemberChatroomRepository memberChatroomRepository;
    private final ChatroomRepository chatroomRepository;

    /**
     * member를 해당 chatroom에 입장 처리하는 메소드
     *
     * @param member
     * @param targetMember
     * @param chatroom
     * @return
     */
    public MemberChatroom enterExistingChatroom(Member member, Member targetMember, Chatroom chatroom) {
        MemberChatroom memberChatroom = memberChatroomRepository
                .findByMemberIdAndChatroomId(member.getId(), chatroom.getId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_ACCESS_DENIED));

        // 내가 해당 채팅방을 퇴장한 상태인 경우
        if (memberChatroom.exited()) {
            // 상대방이 나를 차단한 경우
            blockValidator.throwIfBlocked(targetMember, member, ChatException.class,
                    ErrorCode.CHAT_START_FAILED_BLOCKED_BY_CHAT_TARGET);

            // 상대방이 탈퇴한 경우
            memberValidator.throwIfBlind(targetMember, ChatException.class,
                    ErrorCode.CHAT_START_FAILED_TARGET_USER_DEACTIVATED);
        }

        // lastViewDate 업데이트
        memberChatroom.updateLastViewDate(LocalDateTime.now());

        return memberChatroom;
    }

    /**
     * member와 targetMember 사이 새로운 chatroom 생성 및 저장하는 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    public Chatroom createChatroom(Member member, Member targetMember) {
        Chatroom chatroom = Chatroom.create(UUID.randomUUID().toString());

        chatroomRepository.save(chatroom);

        createAndSaveMemberChatroom(member, chatroom, null);
        createAndSaveMemberChatroom(targetMember, chatroom, null);

        return chatroom;
    }

    /**
     * 해당 회원 및 채팅방에 대한 MemberChatroom 엔티티 생성 및 저장
     *
     * @param member
     * @param chatroom
     * @param lastJoinDate
     */
    private void createAndSaveMemberChatroom(Member member, Chatroom chatroom, LocalDateTime lastJoinDate) {
        memberChatroomRepository.save(MemberChatroom.create(member, chatroom, lastJoinDate));
    }

}
