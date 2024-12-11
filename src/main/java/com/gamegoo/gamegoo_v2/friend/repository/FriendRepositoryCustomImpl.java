package com.gamegoo.gamegoo_v2.friend.repository;

import com.gamegoo.gamegoo_v2.friend.domain.Friend;
import com.gamegoo.gamegoo_v2.friend.domain.QFriend;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

@RequiredArgsConstructor
public class FriendRepositoryCustomImpl implements FriendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Friend> findFriendsByCursorAndOrdered(Long memberId, Long cursor, Integer pageSize) {
        QFriend friend = QFriend.friend;

        List<Friend> friends = queryFactory.selectFrom(friend)
                .where(
                        friend.toMember.id.eq(memberId),
                        cursor != null ? friend.id.gt(cursor) : null // 커서 조건: cursor 이후의 데이터
                )
                .orderBy(
                        // 한글 → 영문자 → 숫자 → 그 외 순으로 정렬
                        new CaseBuilder()
                                .when(stringTemplate("SUBSTRING({0}, 1, 1)", friend.toMember.gameName)
                                        .between("가", "힣")).then(1)
                                .when(stringTemplate("SUBSTRING({0}, 1, 1)", friend.toMember.gameName)
                                        .between("A", "Z")
                                        .or(stringTemplate("SUBSTRING({0}, 1, 1)", friend.toMember.gameName)
                                                .between("a", "z"))).then(2)
                                .when(stringTemplate("SUBSTRING({0}, 1, 1)", friend.toMember.gameName)
                                        .between("0", "9")).then(3)
                                .otherwise(4)
                                .asc(),
                        friend.toMember.gameName.asc(),   // 같은 그룹 내에서는 이름으로 정렬
                        friend.id.asc()      // 중복을 피하기 위해 ID로 정렬
                )
                .limit(pageSize + 1)     // 다음 페이지 여부를 확인하기 위해 pageSize보다 1개 더 가져옴
                .fetch();

        boolean hasNext = friends.size() > pageSize;

        if (hasNext) {
            friends.remove(friends.size() - 1); // 다음 페이지가 있으면 마지막 요소를 제거
        }

        return new SliceImpl<>(friends, Pageable.unpaged(), hasNext);
    }

}
