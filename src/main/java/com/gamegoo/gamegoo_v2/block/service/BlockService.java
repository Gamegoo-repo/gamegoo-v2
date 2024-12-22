package com.gamegoo.gamegoo_v2.block.service;

import com.gamegoo.gamegoo_v2.block.domain.Block;
import com.gamegoo.gamegoo_v2.block.repository.BlockRepository;
import com.gamegoo.gamegoo_v2.common.validator.MemberValidator;
import com.gamegoo.gamegoo_v2.exception.BlockException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;
    private final MemberValidator memberValidator;

    private final static int PAGE_SIZE = 10;

    /**
     * member가 targetMember를 차단 처리하는 메소드
     *
     * @param member
     * @param targetMember
     */
    @Transactional
    public Block blockMember(Member member, Member targetMember) {
        // 본인이 본인을 차단 시도하는 경우 검증
        validateNotSelfBlock(member, targetMember);

        // 대상 회원의 탈퇴 여부 검증
        memberValidator.validateMemberIsNotBlind(targetMember);

        // 이미 차단한 회원인지 검증
        validateNotBlocked(member, targetMember);

        // block 엔티티 생성
        Block block = Block.create(member, targetMember);
        blockRepository.save(block);

        return block;
    }

    /**
     * 해당 회원이 차단한 회원의 목록 Page 객체 반환하는 메소드
     *
     * @param blockerId
     * @param pageIdx
     * @return
     */
    public Page<Member> getBlockedMemberPage(Long blockerId, int pageIdx) {
        PageRequest pageRequest = PageRequest.of(pageIdx - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        return blockRepository.findBlockedMembersByBlockerMember(blockerId, pageRequest);
    }

    /**
     * menber가 targetMember를 차단 해제 처리하는 메소드
     *
     * @param member
     * @param targetMember
     */
    @Transactional
    public Block unBlockMember(Member member, Member targetMember) {
        // 대상 회원의 탈퇴 여부 검증
        memberValidator.validateMemberIsNotBlind(targetMember);

        // targetMember가 실제로 차단 목록에 존재하는지 검증 및 block 엔티티 조회
        Block block = blockRepository.findByBlockerMemberAndBlockedMember(member, targetMember)
                .orElseThrow(() -> new BlockException(ErrorCode.TARGET_MEMBER_NOT_BLOCKED));

        // Block 엔티티의 deleted 필드 업데이트
        block.updateDeleted(true);

        return block;
    }

    /**
     * member의 차단 목록에서 targetMember 삭제
     *
     * @param member
     * @param targetMember
     */
    @Transactional
    public Block deleteBlock(Member member, Member targetMember) {
        // targetMember가 차단 목록에 존재하는지 검증 및 block 엔티티 조회
        Block block = blockRepository.findByBlockerMemberAndBlockedMember(member, targetMember)
                .orElseThrow(() -> new BlockException(ErrorCode.TARGET_MEMBER_NOT_BLOCKED));

        // targetMember가 탈퇴한 회원이 맞는지 검증
        if (!targetMember.isBlind()) {
            throw new BlockException(ErrorCode.DELETE_BLOCKED_MEMBER_FAILED);
        }

        // Block 엔티티의 deleted 필드 업데이트
        block.updateDeleted(true);

        return block;
    }

    /**
     * memer가 targetMember를 차단했는지 여부를 반환하는 메소드
     *
     * @param member
     * @param targetMember
     * @return
     */
    public boolean isBlocked(Member member, Member targetMember) {
        return blockRepository.existsByBlockerMemberAndBlockedMemberAndDeleted(member, targetMember, false);
    }

    private void validateNotSelfBlock(Member member, Member targetMember) {
        if (member.getId().equals(targetMember.getId())) {
            throw new BlockException(ErrorCode.BLOCK_MEMBER_BAD_REQUEST);
        }
    }

    private void validateNotBlocked(Member member, Member targetMember) {
        boolean exists = blockRepository.existsByBlockerMemberAndBlockedMemberAndDeleted(member, targetMember, false);
        if (exists) {
            throw new BlockException(ErrorCode.ALREADY_BLOCKED);
        }
    }

}
