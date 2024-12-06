package com.gamegoo.gamegoo_v2.member.domain;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.common.BaseDateTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 500)
    private String password;

    @Column(nullable = false)
    private int profileImage;

    @Column(nullable = false)
    private int mannerLevel = 1;

    private Integer mannerScore;

    @Column(nullable = false)
    private boolean blind = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private LoginType loginType;

    @Column(nullable = false, length = 100)
    private String gameName;

    @Column(nullable = false, length = 100)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier;

    @Column(nullable = false)
    private int gameRank;

    @Column(nullable = false)
    private double winRate;

    @Column(nullable = false)
    private int mainPosition = 0;

    @Column(nullable = false)
    private int subPosition = 0;

    @Column(nullable = false)
    private int wantPosition = 0;

    @Column(nullable = false)
    private boolean mike = false;

    @Column(nullable = false)
    private int gameCount;

    @Column(nullable = false)
    private boolean isAgree;

    @OneToMany(mappedBy = "blockerMember", cascade = CascadeType.ALL)
    private List<Block> blockList = new ArrayList<>();

    public static Member create(String email, String password, LoginType loginType, String gameName, String tag,
            Tier tier, int gameRank, double winRate, int gameCount, boolean isAgree) {
        int randomProfileImage = ThreadLocalRandom.current().nextInt(1, 9);

        return Member.builder()
                .email(email)
                .password(password)
                .profileImage(randomProfileImage)
                .loginType(loginType)
                .gameName(gameName)
                .tag(tag)
                .tier(tier)
                .gameRank(gameRank)
                .winRate(winRate)
                .gameCount(gameCount)
                .isAgree(isAgree)
                .build();
    }

    @Builder
    private Member(String email, String password, int profileImage, LoginType loginType, String gameName,
            String tag, Tier tier, int gameRank, double winRate, int gameCount, boolean isAgree) {
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.loginType = loginType;
        this.gameName = gameName;
        this.tag = tag;
        this.tier = tier;
        this.gameRank = gameRank;
        this.winRate = winRate;
        this.gameCount = gameCount;
        this.isAgree = isAgree;
    }

    public void updateBlind(boolean blind) {
        this.blind = blind;
    }

}