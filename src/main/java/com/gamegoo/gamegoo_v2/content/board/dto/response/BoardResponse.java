package com.gamegoo.gamegoo_v2.content.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardResponse {

    Integer totalPage;
    Integer totalCount;
    List<BoardListResponse> boards;

}
