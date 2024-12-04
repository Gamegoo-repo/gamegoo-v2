package com.gamegoo.gamegoo_v2.game.domain;

import com.gamegoo.gamegoo_v2.common.BaseDateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Champion extends BaseDateTimeEntity {

    @Id
    @Column(name = "champion_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

}
