package com.gamegoo.gamegoo_v2.social.block.repository;

import java.util.List;
import java.util.Map;

public interface BlockRepositoryCustom {

    Map<Long, Boolean> isBlockedBatch(List<Long> targetMemberIds, Long memberId);

}
