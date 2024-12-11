package com.gamegoo.gamegoo_v2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.Set;

public class RandomCodeGeneratorUtilTest {
    @Test
    void testGenerateEmailRandomCode_LengthAndFormat() {
        // 랜덤 코드 생성
        String randomCode = RandomCodeGeneratorUtil.generateEmailRandomCode();

        // 랜덤 코드 길이 검증
        assertEquals(8, randomCode.length(), "생성된 랜덤코드의 길이가 8이어야한다.");

        // 랜덤 코드 최소 포함 글자 검증
        assertTrue(randomCode.matches(".*[A-Z].*"), "생성된 랜덤코드는 대문자를 하나 반드시 포함해야한다.");
        assertTrue(randomCode.matches(".*[a-z].*"), "생성된 랜덤코드는 소문자를 하나 반드시 포함해야한다.");
        assertTrue(randomCode.matches(".*[0-9].*"), "생성된 랜덤코드는 숫자를 하나 반드시 포함해야한다.");
    }

}
