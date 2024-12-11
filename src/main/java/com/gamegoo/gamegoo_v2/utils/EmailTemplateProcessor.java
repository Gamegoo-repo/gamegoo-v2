package com.gamegoo.gamegoo_v2.utils;

import com.gamegoo.gamegoo_v2.exception.EmailException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class EmailTemplateProcessor {

    /**
     * 템플릿 파일을 로드하고 동적으로 값을 대체
     *
     * @param templatePath 템플릿 파일 경로
     * @param placeholders 대체할 값 (key: placeholder, value: 대체 값)
     * @return 최종 HTML 문자열
     */
    public static String processTemplate(String templatePath, Map<String, String> placeholders) {
        try {
            // 템플릿 파일 읽기
            String templateContent = new String(Files.readAllBytes(Paths.get(templatePath)));

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                templateContent = templateContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return templateContent;
        } catch (IOException e) {
            log.error("이메일 본문 파일 로드에 실패했습니다. : {}", templatePath, e);
            throw new EmailException(ErrorCode.EMAIL_CONTENT_LOAD_FAIL);
        }
    }
}
