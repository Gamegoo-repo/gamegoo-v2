package com.gamegoo.gamegoo_v2.repository.block;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.repository.RepositoryTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class BlockRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private BlockRepository blockRepository;

    private static final int PAGE_SIZE = 10;

    private Member blocker;

    @BeforeEach
    void setUp() {
        blocker = createMember("test@gmail.com", "member");
    }

    @Nested
    @DisplayName("차단한 회원 목록 조회")
    class FindBlockedMembersByBlockerMemberTest {

        @DisplayName("차단한 회원이 없는 경우 빈 page를 반환해야 한다.")
        @Test
        void findBlockedMembersByBlockerMemberNoResult() {
            // when
            Page<Member> blockedMembers = blockRepository.findBlockedMembersByBlockerMember(blocker.getId(),
                    PageRequest.of(0, PAGE_SIZE));

            // then
            assertThat(blockedMembers).isEmpty();
            assertThat(blockedMembers.getTotalElements()).isEqualTo(0);
            assertThat(blockedMembers.getTotalPages()).isEqualTo(0);
            assertThat(blockedMembers.isFirst()).isTrue();
            assertThat(blockedMembers.isLast()).isTrue();
        }

        @DisplayName("차단한 회원이 10명 이하일 때 첫번째 페이지 요청")
        @Test
        void findBlockedMembersByBlockerMemberFirstPage() {
            // given
            for (int i = 1; i <= 10; i++) {
                Member blocked = createMember("member" + i + "@gmail.com", "member" + i);
                blockMember(blocker, blocked);
            }

            // when
            Page<Member> blockedMembers = blockRepository.findBlockedMembersByBlockerMember(blocker.getId(),
                    PageRequest.of(0, PAGE_SIZE));

            // then
            assertThat(blockedMembers).isNotEmpty();
            assertThat(blockedMembers.getTotalElements()).isEqualTo(10);
            assertThat(blockedMembers.getTotalPages()).isEqualTo(1);
            assertThat(blockedMembers.isFirst()).isTrue();
            assertThat(blockedMembers.isLast()).isTrue();
        }

        @DisplayName("차단한 회원이 10명 초과일 때 두번째 페이지 요청")
        @Test
        void findBlockedMembersByBlockerMemberSecondPage() {
            // given
            for (int i = 1; i <= 15; i++) {
                Member blocked = createMember("member" + i + "@gmail.com", "member" + i);
                blockMember(blocker, blocked);
            }

            // when
            Page<Member> blockedMembers = blockRepository.findBlockedMembersByBlockerMember(blocker.getId(),
                    PageRequest.of(1, PAGE_SIZE));

            // then
            assertThat(blockedMembers).isNotEmpty();
            assertThat(blockedMembers.getTotalElements()).isEqualTo(15);
            assertThat(blockedMembers.getTotalPages()).isEqualTo(2);
            assertThat(blockedMembers.isFirst()).isFalse();
            assertThat(blockedMembers.isLast()).isTrue();
        }

        @DisplayName("deleted 상태인 차단 내역을 제외하고 조회해야 한다.")
        @Test
        void findBlockedMembersByBlockerMemberExceptDeleted() {
            // given
            for (int i = 1; i <= 10; i++) {
                Member blocked = createMember("member" + i + "@gmail.com", "member" + i);
                Block block = blockMember(blocker, blocked);
                block.updateDeleted(true);
            }

            // when
            Page<Member> blockedMembers = blockRepository.findBlockedMembersByBlockerMember(blocker.getId(),
                    PageRequest.of(0, PAGE_SIZE));

            // then
            assertThat(blockedMembers).isEmpty();
            assertThat(blockedMembers.getTotalElements()).isEqualTo(0);
            assertThat(blockedMembers.getTotalPages()).isEqualTo(0);
            assertThat(blockedMembers.isFirst()).isTrue();
            assertThat(blockedMembers.isLast()).isTrue();
        }

    }

    private Block blockMember(Member blocker, Member blocked) {
        return em.persist(Block.create(blocker, blocked));
    }

}
