package com.gamegoo.gamegoo_v2.chat.domain;

import lombok.Getter;

@Getter
public enum SystemMessageType {

    INITIATE_CHATROOM_BY_BOARD_MESSAGE(1, "상대방이 게시한 글을 보고 말을 걸었어요. 대화를 시작해보세요~"),
    CHAT_STARTED_BY_BOARD_MESSAGE(2, "상대방이 게시한 글을 보고 말을 걸었어요."),
    INCOMING_CHAT_BY_BOARD_MESSAGE(3, "내가 게시한 글을 보고 말을 걸어왔어요."),
    MATCH_SUCCESS_MESSAGE(4, "상대방과 매칭이 이루어졌어요!");

    private final int code;
    private final String message;

    SystemMessageType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
