package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.FriendRequest;
import com.gamegoo.gamegoo_v2.friend.domain.FriendRequestStatus;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsByFromMemberAndToMemberAndStatus(Member fromMember, Member toMember, FriendRequestStatus status);

    Optional<FriendRequest> findByFromMemberAndToMemberAndStatus(Member fromMember, Member toMember,
            FriendRequestStatus status);

}
