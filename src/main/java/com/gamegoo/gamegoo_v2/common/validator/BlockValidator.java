package com.gamegoo.gamegoo_v2.common.validator;

import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.exception.common.GlobalException;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlockValidator {

    private final BlockRepository blockRepository;

    /**
     * member가 targetMember를 차단한 경우 입력받은 Exception을 발생시키는 메소드
     *
     * @param member
     * @param targetMember
     * @param exceptionClass
     * @param errorCode
     * @param <T>
     */
    public <T extends GlobalException> void throwIfBlocked(Member member, Member targetMember,
            Class<T> exceptionClass, ErrorCode errorCode) {
        boolean exists = blockRepository.existsByBlockerMemberAndBlockedMemberAndDeleted(member, targetMember, false);
        if (exists) {
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
