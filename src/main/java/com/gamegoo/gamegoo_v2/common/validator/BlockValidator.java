package com.gamegoo.gamegoo_v2.common.validator;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class BlockValidator {

    /**
     * member가 targetMember를 차단한 경우, 입력받은 exception을 발생시키는 메소드
     *
     * @param member
     * @param targetMember
     * @param exceptionClass
     * @param errorCode
     * @param <T>
     */
    public <T extends GlobalException> void validateIfBlocked(Member member, Member targetMember,
            Class<T> exceptionClass, ErrorCode errorCode) {
        for (Block block : member.getBlockList()) {
            if (block.getBlockedMember().getId().equals(targetMember.getId())) {
                throw createException(exceptionClass, errorCode);
            }
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
