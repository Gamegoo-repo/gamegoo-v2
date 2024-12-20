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
    MISSING_AUTH_HEADER(UNAUTHORIZED, "AUTH_401", "Authorization 헤더가 없습니다."),
    INVALID_AUTH_HEADER(UNAUTHORIZED, "AUTH_402", "Authorization 헤더가 올바르지 않습니다."),
    INVALID_SIGNATURE(UNAUTHORIZED, "AUTH_403", "JWT 서명이 유효하지 않습니다."),
    MALFORMED_TOKEN(UNAUTHORIZED, "AUTH_404", "JWT의 형식이 올바르지 않습니다."),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "AUTH_405", "지원되지 않는 JWT입니다."),
    EXPIRED_JWT_EXCEPTION(UNAUTHORIZED, "AUTH_406", "기존 토큰이 만료되었습니다. 토큰을 재발급해주세요."),
    INVALID_CLAIMS(UNAUTHORIZED, "AUTH_407", "JWT의 클레임이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "AUTH_408", "리프레쉬 토큰이 만료되었습니다. 다시 로그인 해주세요"),
    UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "AUTH_409", "로그인 후 이용가능합니다. 토큰을 입력해 주세요"),
    MEMBER_EXTRACTION_FAILED(NOT_FOUND, "AUTH_410", "회원 정보를 추출할 수 없습니다."),
    INACTIVE_MEMBER(NOT_FOUND, "AUTH_411", "탈퇴한 사용자 입니다."),

    /**
     * 회원 관련 에러
     */
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_401", "사용자를 찾을 수 없습니다."),
    TARGET_MEMBER_DEACTIVATED(FORBIDDEN, "MEMBER_402", "대상 회원이 탈퇴했습니다."),
    MEMBER_ALREADY_EXISTS(BAD_REQUEST, "MEMBER_403", "이미 존재하는 사용자입니다."),

    /**
     * 이메일 관련 에러
     */
    EMAIL_CONTENT_LOAD_FAIL(NOT_FOUND, "EMAIL401", "이메일 본문을 읽어오는데 실패했습니다."),
    EMAIL_SEND_FAIL(NOT_FOUND, "EMAIL402", "이메일 전송에 실패했습니다"),
    EMAIL_LIMIT_EXCEEDED(BAD_REQUEST, "EMAIL_403", "3분 이내 3개 이상 이메일을 보냈습니다."),
    EMAIL_RECORD_NOT_FOUND(NOT_FOUND, "EMAIL_404", "해당 이메일을 가진 기록이 없습니다."),
    INVALID_VERIFICATION_CODE(BAD_REQUEST, "EMAIL_405", "인증 코드가 틀렸습니다"),
    EMAIL_VERIFICATION_TIME_EXCEED(BAD_REQUEST, "EMAIL_406", "인증 코드 검증 시간을 초과했습니다."),

    /*
     * 차단 관련 에러
     */
    ALREADY_BLOCKED(BAD_REQUEST, "BLOCK_401", "이미 차단한 회원입니다."),
    TARGET_MEMBER_NOT_BLOCKED(BAD_REQUEST, "BLOCK_402", "차단 목록에 존재하지 않는 회원입니다."),
    BLOCK_MEMBER_BAD_REQUEST(BAD_REQUEST, "BLOCK_403", "잘못된 친구 차단 요청입니다."),
    DELETE_BLOCKED_MEMBER_FAILED(FORBIDDEN, "BLOCK_404", "차단 목록에서 삭제 불가한 회원입니다."),

    /**
     * 친구 관련 에러
     */
    FRIEND_BAD_REQUEST(BAD_REQUEST, "FRIEND_401", "잘못된 친구 요청입니다."),
    FRIEND_TARGET_IS_BLOCKED(BAD_REQUEST, "FRIEND_402", "내가 차단한 회원입니다. 친구 요청을 보낼 수 없습니다."),
    BLOCKED_BY_FRIEND_TARGET(BAD_REQUEST, "FRIEND_403", "나를 차단한 회원입니다. 친구 요청을 보낼 수 없습니다."),
    MY_PENDING_FRIEND_REQUEST_EXIST(BAD_REQUEST, "FRIEND_404", "해당 회원에게 보낸 수락 대기 중인 친구 요청이 존재합니다. 친구 요청을 보낼 수 없습니다."),
    TARGET_PENDING_FRIEND_REQUEST_EXIST(BAD_REQUEST, "FRIEND_405", "해당 회원이 나에게 보낸 친구 요청이 수락 대기 중 입니다. 해당 요청을 수락 해주세요."),
    ALREADY_FRIEND(BAD_REQUEST, "FRIEND_406", "두 회원은 이미 친구 관계 입니다. 친구 요청을 보낼 수 없습니다."),
    PENDING_FRIEND_REQUEST_NOT_EXIST(NOT_FOUND, "FRIEND_407", "취소/수락/거절할 친구 요청이 존재하지 않습니다."),
    MEMBERS_NOT_FRIEND(BAD_REQUEST, "FRIEND_408", "두 회원은 친구 관계가 아닙니다."),
    FRIEND_SEARCH_QUERY_BAD_REQUEST(BAD_REQUEST, "FRIEND_409", "친구 검색 쿼리는 100자 이하여야 합니다."),

    /**
     * 알림 관련 에러
     */
    NOTIFICATION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI_401", "해당 알림 타입 데이터를 찾을 수 없습니다."),
    NOTIFICATION_METHOD_BAD_REQUEST(HttpStatus.BAD_REQUEST, "NOTI_402", "알림 생성 메소드 호출이 잘못되었습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI_403", "해당 알림 내역을 찾을 수 없습니다."),

    CHAT_START_FAILED_CHAT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_401",
            "채팅 대상 회원을 찾을 수 없습니다."),
    CHATROOM_NOT_EXIST(HttpStatus.NOT_FOUND, "CHAT_402", "채팅방을 찾을 수 없습니다."),
    CHATROOM_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "CHAT_403", "접근할 수 없는 채팅방 입니다."),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_404", "해당 메시지를 찾을 수 없습니다"),
    CHAT_START_FAILED_CHAT_TARGET_IS_BLOCKED(HttpStatus.FORBIDDEN, "CHAT_405",
            "채팅 상대 회원을 차단한 상태입니다. 채팅 시작이 불가능합니다."),
    CHAT_START_FAILED_BLOCKED_BY_CHAT_TARGET(HttpStatus.FORBIDDEN, "CHAT_406",
            "채팅 상대 회원이 나를 차단했습니다. 채팅 시작이 불가능합니다."),
    CHAT_START_FAILED_TARGET_USER_DEACTIVATED(HttpStatus.NOT_FOUND, "CHAT_407",
            "채팅 상대 회원이 탈퇴했습니다. 채팅 시작이 불가능합니다."),
    CHAT_START_FAILED_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_408",
            "해당 게시글이 존재하지 않습니다. 채팅 시작이 불가능합니다."),
    CHAT_START_FAILED_BOARD_CREATOR_IS_SELF(HttpStatus.BAD_REQUEST, "CHAT_409",
            "해당 게시글의 작성자가 본인입니다. 채팅 시작이 불가능합니다."),
    CHAT_START_FAILED_TARGET_USER_IS_SELF(HttpStatus.BAD_REQUEST, "CHAT_410",
            "채팅 대상 회원이 본인입니다. 채팅 시작이 불가능합니다."),
    CHAT_READ_FAILED_NOT_ENTERED_CHATROOM(HttpStatus.FORBIDDEN, "CHAT_411",
            "해당 채팅방에 입장 상태가 아닙니다. 채팅방 입장 후 메시지 읽음 처리하세요."),
    CHAT_READ_FAILED_CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_412",
            "해당 메시지를 찾을 수 없습니다. 채팅 메시지 읽음 처리 실패"),
    CHAT_ADD_FAILED_TARGET_USER_DEACTIVATED(HttpStatus.BAD_REQUEST, "CHAT_413",
            "채팅 상대 회원이 탈퇴했습니다. 채팅 전송이 불가능합니다."),
    CHAT_ADD_FAILED_CHAT_TARGET_IS_BLOCKED(HttpStatus.FORBIDDEN, "CHAT_414",
            "채팅 상대 회원을 차단한 상태입니다. 채팅 메시지 전송이 불가능합니다."),
    CHAT_ADD_FAILED_BLOCKED_BY_CHAT_TARGET(HttpStatus.FORBIDDEN, "CHAT_415",
            "채팅 상대 회원이 나를 차단했습니다. 채팅 메시지 전송이 불가능합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
