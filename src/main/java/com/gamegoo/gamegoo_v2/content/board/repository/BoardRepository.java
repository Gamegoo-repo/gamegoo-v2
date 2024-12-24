package com.gamegoo.gamegoo_v2.content.board.repository;

import com.gamegoo.gamegoo_v2.content.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
