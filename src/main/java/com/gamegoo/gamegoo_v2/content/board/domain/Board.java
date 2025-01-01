package com.gamegoo.gamegoo_v2.content.board.domain;

import com.gamegoo.gamegoo_v2.core.common.BaseDateTimeEntity;
import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private int mode;

    @Column(nullable = false)
    private int mainPosition;

    @Column(nullable = false)
    private int subPosition;

    @Column(nullable = false)
    private int wantPosition;

    @Column(nullable = false)
    private boolean mike = false;

    @Column(length = 5000)
    private String content;

    @Column(nullable = false)
    private int boardProfileImage;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Board create(Member member, int mode, int mainPosition, int subPosition, int wantPosition,
                               boolean mike, String content, int boardProfileImage) {
        return Board.builder()
                .member(member)
                .mode(mode)
                .mainPosition(mainPosition)
                .subPosition(subPosition)
                .wantPosition(wantPosition)
                .mike(mike)
                .content(content)
                .boardProfileImage(boardProfileImage)
                .build();
    }

    @Builder
    private Board(int mode, int mainPosition, int subPosition, int wantPosition, boolean mike, String content,
                  int boardProfileImage, boolean deleted, Member member) {
        this.mode = mode;
        this.mainPosition = mainPosition;
        this.subPosition = subPosition;
        this.wantPosition = wantPosition;
        this.mike = mike;
        this.content = content;
        this.boardProfileImage = boardProfileImage;
        this.deleted = deleted;
        this.member = member;
    }

}
