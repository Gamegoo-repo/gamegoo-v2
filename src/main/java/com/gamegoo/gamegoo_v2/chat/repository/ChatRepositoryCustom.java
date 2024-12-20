package com.gamegoo.gamegoo_v2.chat.repository;

import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import org.springframework.data.domain.Slice;

public interface ChatRepositoryCustom {

    Slice<Chat> findRecentChats(Long chatroomId, Long memberChatroomId, Long memberId, int pageSize);

}
