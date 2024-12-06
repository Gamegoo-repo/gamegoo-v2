package com.gamegoo.gamegoo_v2.block.repository;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerMemberAndBlockedMember(Member blockerMember, Member blockedMember);

}
