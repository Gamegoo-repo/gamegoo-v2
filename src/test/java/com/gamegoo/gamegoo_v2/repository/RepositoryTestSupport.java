package com.gamegoo.gamegoo_v2.repository;

import com.gamegoo.gamegoo_v2.config.JpaAuditingConfig;
import com.gamegoo.gamegoo_v2.config.QuerydslConfig;
import com.gamegoo.gamegoo_v2.member.domain.LoginType;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.domain.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest(showSql = false)
@Import({QuerydslConfig.class, JpaAuditingConfig.class})
public abstract class RepositoryTestSupport {

    @Autowired
    protected TestEntityManager em;

    protected Member member;

    @BeforeEach
    void setUp() {
        member = createMember("test@gmail.com", "member");
    }

    protected Member createMember(String email, String gameName) {
        return em.persist(Member.builder()
                .email(email)
                .password("testPassword")
                .profileImage(1)
                .loginType(LoginType.GENERAL)
                .gameName(gameName)
                .tag("TAG")
                .tier(Tier.IRON)
                .gameRank(0)
                .winRate(0.0)
                .gameCount(0)
                .isAgree(true)
                .build());
    }

}
