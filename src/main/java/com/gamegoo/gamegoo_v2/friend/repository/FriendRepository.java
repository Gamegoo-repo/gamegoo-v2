package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByFromMemberAndToMember(Member fromMember, Member toMember);

    Friend findByFromMemberAndToMember(Member fromMember, Member toMember);

}
