package com.gamegoo.gamegoo_v2.member.repository;

import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

}
