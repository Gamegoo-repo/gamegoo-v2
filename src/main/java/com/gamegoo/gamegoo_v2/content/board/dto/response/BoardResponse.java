package com.gamegoo.gamegoo_v2.content.board.dto.response;

//import com.gamegoo.gamegoo_v2.social.manner.dto.response.MannerResponse;

import com.gamegoo.gamegoo_v2.account.member.domain.Tier;
import com.gamegoo.gamegoo_v2.game.dto.response.ChampionResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class BoardResponse {

    @Getter
    @NoArgsConstructor
    public static class boardInsertResponseDTO {

        private Long boardId;
        private Long memberId;
        private Integer profileImage;
        private String gameName;
        private String tag;
        private Tier tier;
        private Integer rank;
        private Integer gameMode;
        private Integer mainPosition;
        private Integer subPosition;
        private Integer wantPosition;
        private Boolean mike;
        private List<Long> gameStyles;
        private String contents;

        private boardInsertResponseDTO(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag,
                Tier tier, Integer rank, Integer gameMode, Integer mainPosition, Integer subPosition,
                Integer wantPosition, Boolean mike, List<Long> gameStyles, String contents
        ) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.profileImage = profileImage;
            this.gameName = gameName;
            this.tag = tag;
            this.tier = tier;
            this.rank = rank;
            this.gameMode = gameMode;
            this.mainPosition = mainPosition;
            this.subPosition = subPosition;
            this.wantPosition = wantPosition;
            this.mike = mike;
            this.gameStyles = gameStyles;
            this.contents = contents;
        }

        public static boardInsertResponseDTO create(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag,
                Tier tier, Integer rank, Integer gameMode, Integer mainPosition, Integer subPosition,
                Integer wantPosition, Boolean mike, List<Long> gameStyles, String contents
        ) {
            return new boardInsertResponseDTO(
                    boardId, memberId, profileImage, gameName, tag, tier, rank, gameMode,
                    mainPosition, subPosition, wantPosition, mike, gameStyles, contents
            );
        }

    }

    @Getter
    @NoArgsConstructor
    public static class boardUpdateResponseDTO {

        private Long boardId;
        private Long memberId;
        private Integer profileImage;
        private String gameName;
        private String tag;
        private Tier tier;
        private Integer rank;
        private Integer gameMode;
        private Integer mainPosition;
        private Integer subPosition;
        private Integer wantPosition;
        private Boolean mike;
        private List<Long> gameStyles;
        private String contents;

        private boardUpdateResponseDTO(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag,
                Tier tier, Integer rank, Integer gameMode, Integer mainPosition, Integer subPosition,
                Integer wantPosition, Boolean mike, List<Long> gameStyles, String contents
        ) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.profileImage = profileImage;
            this.gameName = gameName;
            this.tag = tag;
            this.tier = tier;
            this.rank = rank;
            this.gameMode = gameMode;
            this.mainPosition = mainPosition;
            this.subPosition = subPosition;
            this.wantPosition = wantPosition;
            this.mike = mike;
            this.gameStyles = gameStyles;
            this.contents = contents;
        }

        public static boardUpdateResponseDTO create(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag,
                Tier tier, Integer rank, Integer gameMode, Integer mainPosition, Integer subPosition,
                Integer wantPosition, Boolean mike, List<Long> gameStyles, String contents
        ) {
            return new boardUpdateResponseDTO(
                    boardId, memberId, profileImage, gameName, tag, tier, rank, gameMode,
                    mainPosition, subPosition, wantPosition, mike, gameStyles, contents
            );
        }

    }

    @Getter
    @NoArgsConstructor
    public static class boardResponseDTO {

        private Integer totalPage;
        private Integer totalCount;
        private List<boardListResponseDTO> boards;

        private boardResponseDTO(Integer totalPage, Integer totalCount, List<boardListResponseDTO> boards) {
            this.totalPage = totalPage;
            this.totalCount = totalCount;
            this.boards = boards;
        }

        public static boardResponseDTO create(Integer totalPage, Integer totalCount,
                                              List<boardListResponseDTO> boards) {
            return new boardResponseDTO(totalPage, totalCount, boards);
        }

    }

    @Getter
    @NoArgsConstructor
    public static class boardListResponseDTO {

        private Long boardId;
        private Long memberId;
        private Integer profileImage;
        private String gameName;
        private String tag;
        private Integer mannerLevel;
        private Tier tier;
        private Integer rank;
        private Integer gameMode;
        private Integer mainPosition;
        private Integer subPosition;
        private Integer wantPosition;
        private List<ChampionResponse> championResponseDTOList;
        private Double winRate;
        private LocalDateTime createdAt;
        private Boolean mike;

        private boardListResponseDTO(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag,
                Integer mannerLevel, Tier tier, Integer rank, Integer gameMode, Integer mainPosition,
                Integer subPosition, Integer wantPosition, List<ChampionResponse> championResponseDTOList,
                Double winRate, LocalDateTime createdAt, Boolean mike
        ) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.profileImage = profileImage;
            this.gameName = gameName;
            this.tag = tag;
            this.mannerLevel = mannerLevel;
            this.tier = tier;
            this.rank = rank;
            this.gameMode = gameMode;
            this.mainPosition = mainPosition;
            this.subPosition = subPosition;
            this.wantPosition = wantPosition;
            this.championResponseDTOList = championResponseDTOList;
            this.winRate = winRate;
            this.createdAt = createdAt;
            this.mike = mike;
        }

        public static boardListResponseDTO create(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag,
                Integer mannerLevel, Tier tier, Integer rank, Integer gameMode, Integer mainPosition,
                Integer subPosition, Integer wantPosition, List<ChampionResponse> championResponseDTOList,
                Double winRate, LocalDateTime createdAt, Boolean mike
        ) {
            return new boardListResponseDTO(
                    boardId, memberId, profileImage, gameName, tag, mannerLevel, tier, rank,
                    gameMode, mainPosition, subPosition, wantPosition, championResponseDTOList,
                    winRate, createdAt, mike
            );
        }

    }

    @Getter
    @NoArgsConstructor
    public static class boardByIdResponseDTO {

        private Long boardId;
        private Long memberId;
        private LocalDateTime createdAt;
        private Integer profileImage;
        private String gameName;
        private String tag;
        private Integer mannerLevel;
        private Tier tier;
        private Integer rank;
        private Boolean mike;
        private List<ChampionResponse> championResponseDTOList;
        private Integer gameMode;
        private Integer mainPosition;
        private Integer subPosition;
        private Integer wantPosition;
        private Integer recentGameCount;
        private Double winRate;
        private List<Long> gameStyles;
        private String contents;

        private boardByIdResponseDTO(
                Long boardId, Long memberId, LocalDateTime createdAt, Integer profileImage, String gameName,
                String tag, Integer mannerLevel, Tier tier, Integer rank, Boolean mike,
                List<ChampionResponse> championResponseDTOList, Integer gameMode, Integer mainPosition,
                Integer subPosition, Integer wantPosition, Integer recentGameCount, Double winRate,
                List<Long> gameStyles, String contents
        ) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.createdAt = createdAt;
            this.profileImage = profileImage;
            this.gameName = gameName;
            this.tag = tag;
            this.mannerLevel = mannerLevel;
            this.tier = tier;
            this.rank = rank;
            this.mike = mike;
            this.championResponseDTOList = championResponseDTOList;
            this.gameMode = gameMode;
            this.mainPosition = mainPosition;
            this.subPosition = subPosition;
            this.wantPosition = wantPosition;
            this.recentGameCount = recentGameCount;
            this.winRate = winRate;
            this.gameStyles = gameStyles;
            this.contents = contents;
        }

        public static boardByIdResponseDTO create(
                Long boardId, Long memberId, LocalDateTime createdAt, Integer profileImage, String gameName,
                String tag, Integer mannerLevel, Tier tier, Integer rank, Boolean mike,
                List<ChampionResponse> championResponseDTOList, Integer gameMode, Integer mainPosition,
                Integer subPosition, Integer wantPosition, Integer recentGameCount, Double winRate,
                List<Long> gameStyles, String contents
        ) {
            return new boardByIdResponseDTO(
                    boardId, memberId, createdAt, profileImage, gameName, tag, mannerLevel, tier, rank, mike,
                    championResponseDTOList, gameMode, mainPosition, subPosition, wantPosition, recentGameCount,
                    winRate, gameStyles, contents
            );
        }

    }

    @Getter
    @NoArgsConstructor
    public static class boardByIdResponseForMemberDTO {

        private Long boardId;
        private Long memberId;
        private Boolean isBlocked;
        private Boolean isFriend;
        private Long friendRequestMemberId;
        private LocalDateTime createdAt;
        private Integer profileImage;
        private String gameName;
        private String tag;
        private Integer mannerLevel;
        //private List<MannerResponse.mannerKeywordDTO> mannerKeywords; // 추후 구현
        private Tier tier;
        private Integer rank;
        private Boolean mike;
        private List<ChampionResponse> championResponseDTOList;
        private Integer gameMode;
        private Integer mainPosition;
        private Integer subPosition;
        private Integer wantPosition;
        private Integer recentGameCount;
        private Double winRate;
        private List<Long> gameStyles;
        private String contents;

        private boardByIdResponseForMemberDTO(
                Long boardId, Long memberId, Boolean isBlocked, Boolean isFriend, Long friendRequestMemberId,
                LocalDateTime createdAt, Integer profileImage, String gameName, String tag, Integer mannerLevel,
                /*List<MannerResponse.mannerKeywordDTO> mannerKeywords,*/ Tier tier, Integer rank, Boolean mike,
                List<ChampionResponse> championResponseDTOList,
                Integer gameMode, Integer mainPosition, Integer subPosition, Integer wantPosition,
                Integer recentGameCount, Double winRate, List<Long> gameStyles, String contents
        ) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.isBlocked = isBlocked;
            this.isFriend = isFriend;
            this.friendRequestMemberId = friendRequestMemberId;
            this.createdAt = createdAt;
            this.profileImage = profileImage;
            this.gameName = gameName;
            this.tag = tag;
            this.mannerLevel = mannerLevel;
            this.tier = tier;
            this.rank = rank;
            this.mike = mike;
            this.championResponseDTOList = championResponseDTOList;
            this.gameMode = gameMode;
            this.mainPosition = mainPosition;
            this.subPosition = subPosition;
            this.wantPosition = wantPosition;
            this.recentGameCount = recentGameCount;
            this.winRate = winRate;
            this.gameStyles = gameStyles;
            this.contents = contents;
        }

        public static boardByIdResponseForMemberDTO create(
                Long boardId, Long memberId, Boolean isBlocked, Boolean isFriend, Long friendRequestMemberId,
                LocalDateTime createdAt, Integer profileImage, String gameName, String tag, Integer mannerLevel,
                Tier tier, Integer rank, Boolean mike, List<ChampionResponse> championResponseDTOList,
                Integer gameMode, Integer mainPosition, Integer subPosition, Integer wantPosition,
                Integer recentGameCount, Double winRate, List<Long> gameStyles, String contents
        ) {
            return new boardByIdResponseForMemberDTO(
                    boardId, memberId, isBlocked, isFriend, friendRequestMemberId, createdAt, profileImage, gameName,
                    tag, mannerLevel, tier, rank, mike, championResponseDTOList, gameMode, mainPosition, subPosition,
                    wantPosition, recentGameCount, winRate, gameStyles, contents
            );
        }

    }

    @Getter
    @NoArgsConstructor
    public static class myBoardResponseDTO {

        private Integer totalPage;
        private Integer totalCount;
        private List<myBoardListResponseDTO> myBoards;

        private myBoardResponseDTO(Integer totalPage, Integer totalCount, List<myBoardListResponseDTO> myBoards) {
            this.totalPage = totalPage;
            this.totalCount = totalCount;
            this.myBoards = myBoards;
        }

        public static myBoardResponseDTO create(Integer totalPage, Integer totalCount,
                                                List<myBoardListResponseDTO> myBoards) {
            return new myBoardResponseDTO(totalPage, totalCount, myBoards);
        }

    }

    @Getter
    @NoArgsConstructor
    public static class myBoardListResponseDTO {

        private Long boardId;
        private Long memberId;
        private Integer profileImage;
        private String gameName;
        private String tag;
        private Tier tier;
        private Integer rank;
        private String contents;
        private LocalDateTime createdAt;

        private myBoardListResponseDTO(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag, Tier tier,
                Integer rank, String contents, LocalDateTime createdAt
        ) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.profileImage = profileImage;
            this.gameName = gameName;
            this.tag = tag;
            this.tier = tier;
            this.rank = rank;
            this.contents = contents;
            this.createdAt = createdAt;
        }

        public static myBoardListResponseDTO create(
                Long boardId, Long memberId, Integer profileImage, String gameName, String tag, Tier tier,
                Integer rank, String contents, LocalDateTime createdAt
        ) {
            return new myBoardListResponseDTO(boardId, memberId, profileImage, gameName, tag, tier, rank, contents,
                    createdAt);
        }

    }

}
