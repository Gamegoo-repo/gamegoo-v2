package com.gamegoo.gamegoo_v2.account.member.repository;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

}
