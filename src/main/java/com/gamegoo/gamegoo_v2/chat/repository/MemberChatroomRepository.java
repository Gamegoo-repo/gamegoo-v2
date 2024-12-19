package com.gamegoo.gamegoo_v2.chat.repository;

import com.gamegoo.gamegoo_v2.chat.domain.MemberChatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChatroomRepository extends JpaRepository<MemberChatroom, Long> {

    Optional<MemberChatroom> findByMemberIdAndChatroomId(Long memberId, Long chatroomId);

}
