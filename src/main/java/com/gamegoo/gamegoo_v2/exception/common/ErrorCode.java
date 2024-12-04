package com.gamegoo.gamegoo_v2.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    _INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    /**
     * 인증 관련 에러
     */
    MISSING_AUTH_HEADER(UNAUTHORIZED, "AUTH_4001", "Authorization 헤더가 없습니다."),
    INVALID_AUTH_HEADER(UNAUTHORIZED, "AUTH_4002", "Authorization 헤더가 올바르지 않습니다."),
    INVALID_SIGNATURE(UNAUTHORIZED, "AUTH_4003", "JWT 서명이 유효하지 않습니다."),
    MALFORMED_TOKEN(UNAUTHORIZED, "AUTH_4004", "JWT의 형식이 올바르지 않습니다."),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "AUTH_4005", "지원되지 않는 JWT입니다."),
    EXPIRED_JWT_EXCEPTION(UNAUTHORIZED, "AUTH_4006", "기존 토큰이 만료되었습니다. 토큰을 재발급해주세요."),
    INVALID_CLAIMS(UNAUTHORIZED, "AUTH_4007", "JWT의 클레임이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "AUTH_4008", "리프레쉬 토큰이 만료되었습니다. 다시 로그인 해주세요"),
    UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "AUTH_4009", "로그인 후 이용가능합니다. 토큰을 입력해 주세요"),
    MEMBER_EXTRACTION_FAILED(NOT_FOUND, "AUTH_4010", "회원 정보를 추출할 수 없습니다."),
    INACTIVE_MEMBER(NOT_FOUND, "AUTH_4011", "탈퇴한 사용자 입니다."),

    /**
     * 회원 관련 에러
     */
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_4001", "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
