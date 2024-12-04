package com.gamegoo.gamegoo_v2.auth.jwt;

import com.gamegoo.gamegoo_v2.exception.JwtAuthException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenExpTime;

    @Value("${jwt.refresh_expiration_day}")
    private long refreshExpireDay;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * access token 생성 메소드
     *
     * @param memberId
     * @return
     */
    public String createAccessToken(Long memberId) {
        return createToken(memberId, accessTokenExpTime);
    }

    /**
     * refresh token 생성 메소드
     *
     * @param memberId
     * @return
     */
    public String createRefreshToken(Long memberId) {
        // refreshExpireDay를 밀리초 단위로 변환
        long refreshExpireTimeInMillis = refreshExpireDay * 24 * 60 * 60 * 1000;

        return createToken(memberId, refreshExpireTimeInMillis);
    }

    /**
     * token claim에서 만료 시간 추출 메소드
     *
     * @param token
     * @return
     */
    public Long getTokenExpirationTime(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    /**
     * token claim에서 memberId 추출 메소드
     *
     * @param token
     * @return
     */
    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    /**
     * request의 header에서 token 값 추출 메소드
     *
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(bearerToken)) {
            // Authorization 헤더가 없는 경우
            throw new JwtAuthException(ErrorCode.MISSING_AUTH_HEADER);
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            // Authorization 헤더 형식이 잘못된 경우
            throw new JwtAuthException(ErrorCode.INVALID_AUTH_HEADER);
        }

        return bearerToken.substring(7);
    }

    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException e) {
            throw new JwtAuthException(ErrorCode.INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new JwtAuthException(ErrorCode.MALFORMED_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new JwtAuthException(ErrorCode.EXPIRED_JWT_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthException(ErrorCode.INVALID_CLAIMS);
        }
    }

    /**
     * token 생성 메소드
     *
     * @param memberId
     * @param expireTime
     * @return
     */
    private String createToken(Long memberId, Long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);

        long now = (new Date()).getTime();
        Date validity = new Date(now + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT Claims 추출 메소드
     *
     * @param accessToken
     * @return JWT Claims
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
