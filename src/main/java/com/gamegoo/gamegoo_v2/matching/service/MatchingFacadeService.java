package com.gamegoo.gamegoo_v2.matching.service;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;
import com.gamegoo.gamegoo_v2.chat.service.ChatCommandService;
import com.gamegoo.gamegoo_v2.chat.service.ChatQueryService;
import com.gamegoo.gamegoo_v2.core.common.validator.BlockValidator;
import com.gamegoo.gamegoo_v2.core.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.core.exception.ChatException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingFacadeService {

    private final ChatQueryService chatQueryService;
    private final ChatCommandService chatCommandService;
    private final MemberValidator memberValidator;
    private final BlockValidator blockValidator;

    /**
     * 두 회원 사이 매칭을 통한 채팅방 시작 Facade 메소드
     *
     * @param member1 회원
     * @param member2 회원
     * @return 채팅방 uuid
     */
    @Transactional
    public String startChatroomByMatching(Member member1, Member member2) {
        memberValidator.throwIfEqual(member1, member2);

        // 탈퇴하지 않았는지 검증
        memberValidator.throwIfBlind(member1);
        memberValidator.throwIfBlind(member2);

        // 서로의 차단 여부 검증
        validateBlockStatus(member1, member2);

        // 채팅방 조회, 생성 및 입장 처리
        Chatroom chatroom = chatQueryService.findExistingChatroom(member1, member2)
                .orElseGet(() -> chatCommandService.createChatroom(member1, member2));

        chatCommandService.updateLastJoinDate(member1, chatroom.getId(), LocalDateTime.now());
        chatCommandService.updateLastJoinDate(member2, chatroom.getId(), LocalDateTime.now());

        // 매칭 시스템 메시지 생성
        chatCommandService.createMatchingSystemChat(member1, member2, chatroom);

        return chatroom.getUuid();
    }

    /**
     * 두 회원의 서로 차단 여부 검증
     *
     * @param member1 회원
     * @param member2 회원
     */
    private void validateBlockStatus(Member member1, Member member2) {
        blockValidator.throwIfBlocked(member1, member2, ChatException.class,
                ErrorCode.CHAT_START_FAILED_TARGET_IS_BLOCKED);
        blockValidator.throwIfBlocked(member2, member1, ChatException.class,
                ErrorCode.CHAT_START_FAILED_BLOCKED_BY_TARGET);
    }

}
