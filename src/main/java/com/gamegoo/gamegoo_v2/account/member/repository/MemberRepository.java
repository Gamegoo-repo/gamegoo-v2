package com.gamegoo.gamegoo_v2.account.member.repository;

import com.gamegoo.gamegoo_v2.account.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

}
