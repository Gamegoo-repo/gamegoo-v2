package com.gamegoo.gamegoo_v2.chat.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class ChatMessageListResponse {

    List<ChatMessageResponse> chatMessageList;
    int listSize;
    boolean hasNext;
    Long nextCursor;

}
