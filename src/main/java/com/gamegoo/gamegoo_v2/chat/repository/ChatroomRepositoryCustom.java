package com.gamegoo.gamegoo_v2.chat.repository;

import com.gamegoo.gamegoo_v2.chat.domain.Chatroom;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepositoryCustom {

    Optional<Chatroom> findChatroomByMemberIds(Long memberId1, Long memberId2);

    List<Chatroom> findActiveChatrooms(Long memberId);

}
