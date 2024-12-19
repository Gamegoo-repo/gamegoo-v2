package com.gamegoo.gamegoo_v2.common.validator;

import com.gamegoo.gamegoo_v2.exception.MemberException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberValidator {

    /**
     * 대상 회원이 탈퇴하지 않았는지 검증
     *
     * @param member
     */
    public void validateMemberIsNotBlind(Member member) {
        if (member.isBlind()) {
            throw new MemberException(ErrorCode.TARGET_MEMBER_DEACTIVATED);
        }
    }

    /**
     * 두 회원이 서로 다른 회원인지 검증
     *
     * @param member1
     * @param member2
     */
    public void validateDifferentMembers(Member member1, Member member2) {
        if (member1.getId().equals(member2.getId())) {
            throw new GlobalException(ErrorCode._BAD_REQUEST);
        }
    }

    /**
     * 해당 회원이 탈퇴한 상태인 경우 입력받은 Exception을 발생시키는 메소드
     *
     * @param member
     * @param exceptionClass
     * @param errorCode
     * @param <T>
     */
    public <T extends GlobalException> void throwIfBlind(Member member, Class<T> exceptionClass, ErrorCode errorCode) {
        if (member.isBlind()) {
            throw createException(exceptionClass, errorCode);
        }
    }

    private <T extends GlobalException> T createException(Class<T> exceptionClass, ErrorCode errorCode) {
        try {
            return exceptionClass.getConstructor(ErrorCode.class).newInstance(errorCode);
        } catch (Exception e) {
            throw new RuntimeException("Exception instantiation failed", e);
        }
    }

}
