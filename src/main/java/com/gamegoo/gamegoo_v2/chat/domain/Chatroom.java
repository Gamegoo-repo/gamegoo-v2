package com.gamegoo.gamegoo_v2.chat.domain;

import com.gamegoo.gamegoo_v2.common.BaseDateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Column(nullable = false)
    private String uuid;

    public static Chatroom create(String uuid) {
        return Chatroom.builder()
                .uuid(uuid)
                .build();
    }

    @Builder
    private Chatroom(String uuid) {
        this.uuid = uuid;
    }

}
