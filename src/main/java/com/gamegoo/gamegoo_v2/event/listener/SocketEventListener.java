package com.gamegoo.gamegoo_v2.event.listener;

import com.gamegoo.gamegoo_v2.event.SocketJoinEvent;
import com.gamegoo.gamegoo_v2.socket.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketEventListener {

    private final SocketService socketService;

    /**
     * socket 서버로 join API 요청 event listener
     *
     * @param event
     */
    @Async
    @EventListener
    public void handleSocketJoinEvent(SocketJoinEvent event) {
        socketService.joinSocketToChatroom(event.getMemberId(), event.getUuid());
    }

}
