package com.gamegoo.gamegoo_v2.matching.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum GameMode {
    FAST(1),
    SOLO(2),
    FREE(3),
    ARAM(4);

    private final int id;

    GameMode(int id) {
        this.id = id;
    }

    // id로 GameMode 객체 조회하기 위한 map
    private static final Map<Integer, GameMode> GAME_MODE_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(GameMode::getId, gameMode -> gameMode));

    /**
     * id에 해당하는 GameMode Enum을 리턴하는 메소드
     *
     * @param id
     * @return
     */
    public static GameMode of(int id) {
        GameMode gameMode = GAME_MODE_MAP.get(id);
        if (gameMode == null) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return gameMode;
    }
}
