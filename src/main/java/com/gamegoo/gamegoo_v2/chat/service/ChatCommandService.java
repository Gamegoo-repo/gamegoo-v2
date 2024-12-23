package com.gamegoo.gamegoo_v2.chat.service;

import com.gamegoo.gamegoo_v2.board.domain.Board;
import com.gamegoo.gamegoo_v2.board.repository.BoardRepository;
import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import com.gamegoo.gamegoo_v2.chat.domain.SystemMessageType;
import com.gamegoo.gamegoo_v2.chat.dto.request.SystemFlagRequest;
import com.gamegoo.gamegoo_v2.chat.repository.ChatRepository;
import com.gamegoo.gamegoo_v2.chat.repository.ChatroomRepository;
import com.gamegoo.gamegoo_v2.chat.repository.MemberChatroomRepository;
import com.gamegoo.gamegoo_v2.common.validator.BlockValidator;
import com.gamegoo.gamegoo_v2.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.event.SocketJoinEvent;
import com.gamegoo.gamegoo_v2.exception.ChatException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final MemberChatroomRepository memberChatroomRepository;
    private final ChatroomRepository chatroomRepository;
    private final BoardRepository boardRepository;
    private final ApplicationEventPublisher eventPublisher;


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
                    ErrorCode.CHAT_START_FAILED_BLOCKED_BY_TARGET);

            // 상대방이 탈퇴한 경우
            memberValidator.throwIfBlind(targetMember, ChatException.class,
                    ErrorCode.CHAT_START_FAILED_TARGET_DEACTIVATED);
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
     * 시스템 메시지 등록 처리 메소드
     *
     * @param request
     * @param member
     * @param targetMember
     * @param chatroom
     */
    public void processSystemMessages(SystemFlagRequest request, Member member, Member targetMember,
                                      Chatroom chatroom) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new ChatException(ErrorCode.ADD_BOARD_SYSTEM_CHAT_FAILED));

        // member에게 보낼 시스템 메시지 생성 및 저장
        SystemMessageType systemType = SystemMessageType.of(request.getFlag());
        String memberMessage = systemType.getMessage();
        Chat systemChatToMember = createAndSaveSystemChat(chatroom, member, memberMessage, board, systemType.getCode());

        // targetMember에게 보낼 시스템 메시지 생성 및 저장
        SystemMessageType targetSystemType = SystemMessageType.INCOMING_CHAT_BY_BOARD_MESSAGE;
        Chat systemChatToTargetMember = createAndSaveSystemChat(chatroom, targetMember, targetSystemType.getMessage(),
                board, targetSystemType.getCode());

        // lastJoinDate 업데이트
        updateLastJoinDates(member, targetMember, systemChatToMember.getCreatedAt(),
                systemChatToTargetMember.getCreatedAt(), chatroom);
    }

    /**
     * MemberChatroom의 lastViewDate 및 lastJoinDate 업데이트
     *
     * @param chat
     * @param member
     * @param targetMember
     * @param isSystemMessage
     */
    public void updateMemberChatroomDates(Chat chat, Member member, Member targetMember, boolean isSystemMessage) {
        MemberChatroom memberChatroom = memberChatroomRepository
                .findByMemberIdAndChatroomId(member.getId(), chat.getChatroom().getId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_ACCESS_DENIED));

        if (isSystemMessage) {
            memberChatroom.updateLastViewDate(chat.getCreatedAt());
        } else {
            updateLastViewAndJoinDates(memberChatroom, chat.getCreatedAt());
            MemberChatroom targetMemberChatroom = memberChatroomRepository
                    .findByMemberIdAndChatroomId(targetMember.getId(), chat.getChatroom().getId())
                    .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_ACCESS_DENIED));

            if (targetMemberChatroom.getLastJoinDate() == null) {
                updateLastViewAndJoinDates(targetMemberChatroom, chat.getCreatedAt());
            }
        }
    }

    /**
     * 회원 메시지 생성 및 저장 메소드
     *
     * @param member
     * @param chatroom
     * @param content
     * @return
     */
    public Chat createMemberChat(Member member, Chatroom chatroom, String content) {
        return chatRepository.save(Chat.builder()
                .chatroom(chatroom)
                .fromMember(member)
                .contents(content)
                .build());
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

    /**
     * 시스템 메시지 생성 및 저장
     *
     * @param chatroom
     * @param toMember
     * @param content
     * @param sourceBoard
     * @return
     */
    private Chat createAndSaveSystemChat(Chatroom chatroom, Member toMember, String content, Board sourceBoard,
                                         int systemType) {
        Member systemMember = memberRepository.findById(0L)
                .orElseThrow(() -> new ChatException(ErrorCode.SYSTEM_MEMBER_NOT_FOUND));

        return chatRepository.save(Chat.builder()
                .contents(content)
                .chatroom(chatroom)
                .fromMember(systemMember)
                .toMember(toMember)
                .sourceBoard(sourceBoard)
                .systemType(systemType)
                .build());
    }

    /**
     * 회원과 상대 회원의 lastJoinDate 업데이트 및 socket join 이벤트 발생
     *
     * @param member
     * @param targetMember
     * @param memberCreatedAt
     * @param targetCreatedAt
     * @param chatroom
     */
    private void updateLastJoinDates(Member member, Member targetMember, LocalDateTime memberCreatedAt,
                                     LocalDateTime targetCreatedAt, Chatroom chatroom) {
        MemberChatroom memberChatroom = memberChatroomRepository
                .findByMemberIdAndChatroomId(member.getId(), chatroom.getId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_ACCESS_DENIED));

        MemberChatroom targetMemberChatroom = memberChatroomRepository
                .findByMemberIdAndChatroomId(targetMember.getId(), chatroom.getId())
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_ACCESS_DENIED));

        if (memberChatroom.getLastJoinDate() == null) {
            memberChatroom.updateLastJoinDate(memberCreatedAt);

            // socket join API 요청
            eventPublisher.publishEvent(new SocketJoinEvent(member.getId(), chatroom.getUuid()));
        }

        if (targetMemberChatroom.getLastJoinDate() == null) {
            targetMemberChatroom.updateLastJoinDate(targetCreatedAt);

            // socket join API 요청
            eventPublisher.publishEvent(new SocketJoinEvent(targetMember.getId(), chatroom.getUuid()));
        }
    }

    /**
     * lastViewDate 및 lastJoinDate 업데이트
     *
     * @param chatroom 회원의 MemberChatroom 엔티티
     * @param date     업데이트할 날짜
     */
    private void updateLastViewAndJoinDates(MemberChatroom chatroom, LocalDateTime date) {
        chatroom.updateLastViewDate(date);
        if (chatroom.getLastJoinDate() == null) {
            chatroom.updateLastJoinDate(date);

            eventPublisher.publishEvent(
                    new SocketJoinEvent(chatroom.getMember().getId(), chatroom.getChatroom().getUuid()));
        }
    }

}
