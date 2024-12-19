package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {

    boolean existsByFromMemberAndToMember(Member fromMember, Member toMember);

    Optional<Friend> findByFromMemberAndToMember(Member fromMember, Member toMember);

}
