package com.gamegoo.gamegoo_v2.chat.repository;

import com.gamegoo.gamegoo_v2.chat.domain.Chat;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface ChatRepositoryCustom {

    Slice<Chat> findRecentChats(Long chatroomId, Long memberId, int pageSize);

    Slice<Chat> findChatsByCursor(Long cursor, Long chatroomId, Long memberId, int pageSize);

    int countUnreadChats(Long chatroomId, Long memberId);

    Map<Long, Integer> countUnreadChatsBatch(List<Long> chatroomIds, Long memberId);

}
