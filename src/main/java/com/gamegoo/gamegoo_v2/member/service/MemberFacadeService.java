package com.gamegoo.gamegoo_v2.member.service;

import com.gamegoo.gamegoo_v2.member.domain.Member;
import com.gamegoo.gamegoo_v2.member.dto.response.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFacadeService {

    /**
     * 프로필 조회
     *
     * @param member 조회할 회원
     * @return 조회된 결과 DTO
     */
    public MyProfileResponse getMyProfile(Member member) {

        //TODO: mannerRank 로직 추가

        return MyProfileResponse.of(member, 1.0);
    }

}
