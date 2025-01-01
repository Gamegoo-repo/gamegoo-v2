package com.gamegoo.gamegoo_v2.account.auth.service;

import com.gamegoo.gamegoo_v2.account.auth.domain.RefreshToken;
import com.gamegoo.gamegoo_v2.account.auth.repository.RefreshTokenRepository;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.core.exception.MemberException;
import com.gamegoo.gamegoo_v2.core.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {


    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 리프레시 토큰 제거
     *
     * @param member 로그아웃한 회원
     */
    public void deleteRefreshToken(Member member) {
        RefreshToken refreshToken = refreshTokenRepository.findByMember(member)
                .orElseThrow(); // 리프레시 토큰이 없을 경우 에러를 발생시킬 필요가 없음. 이미 삭제된 상태

        refreshTokenRepository.delete(refreshToken);
    }

    /**
     * 리프레시 토큰 생성
     *
     * @param member       로그인한 회원
     * @param refreshToken 리프레시토큰 정보
     */
    public void addRefreshToken(Member member, String refreshToken) {
        // 이전에 있던 refreshToken 전부 지우기
        refreshTokenRepository.findByMember(member).ifPresent(refreshTokenRepository::delete);

        // refresh token DB에 저장하기
        refreshTokenRepository.save(RefreshToken.create(refreshToken, member));
    }

    /**
     * 비밀번호 검증
     *
     * @param member   회원
     * @param password 비밀번호
     */
    public void verifyPassword(Member member, String password) {
        if (!PasswordUtil.matchesPassword(password, member.getPassword())) {
            throw new MemberException(ErrorCode.INVALID_PASSWORD);
        }
    }


}
