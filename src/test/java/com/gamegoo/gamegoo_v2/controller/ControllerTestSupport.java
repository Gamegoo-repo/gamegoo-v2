package com.gamegoo.gamegoo_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamegoo.gamegoo_v2.account.auth.annotation.resolver.AuthMemberArgumentResolver;
import com.gamegoo.gamegoo_v2.account.auth.jwt.JwtInterceptor;
import com.gamegoo.gamegoo_v2.account.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import com.gamegoo.gamegoo_v2.account.member.domain.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@WebMvcTest
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected JwtInterceptor jwtInterceptor;

    @MockitoBean
    protected AuthMemberArgumentResolver authMemberArgumentResolver;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Member mockMember;

    protected static final String MOCK_EMAIL = "test@gmail.com";
    protected static final String MOCK_PASSWORD = "mockpassword";
    protected static final int MOCK_PROFILE_IMG = 1;
    protected static final LoginType MOCK_LOGIN_TYPE = LoginType.GENERAL;
    protected static final String MOCK_GAMENAME = "gamename";
    protected static final String MOCK_TAG = "KR1";
    protected static final Tier MOCK_TIER = Tier.BRONZE;
    protected static final int MOCK_GAME_RANK = 1;
    protected static final double MOCK_WIN_RATE = 50.0;
    protected static final int MOCK_GAME_COUNT = 10;
    protected static final boolean MOCK_IS_AGREE = true;

    @BeforeEach
    public void setUp() throws Exception {
        // 인터셉터가 항상 true를 반환하도록 Mock 설정
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // authMemberArgumentResolver가 mockMember를 반환하도록 Mock 설정
        mockMember = Member.builder()
                .email(MOCK_EMAIL)
                .password(MOCK_PASSWORD)
                .profileImage(MOCK_PROFILE_IMG)
                .loginType(MOCK_LOGIN_TYPE)
                .gameName(MOCK_GAMENAME)
                .tag(MOCK_TAG)
                .tier(MOCK_TIER)
                .gameRank(MOCK_GAME_RANK)
                .winRate(MOCK_WIN_RATE)
                .gameCount(MOCK_GAME_COUNT)
                .isAgree(MOCK_IS_AGREE)
                .build();
        given(authMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockMember);
    }

}
