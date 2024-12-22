package com.gamegoo.gamegoo_v2.riot.service;

import com.gamegoo.gamegoo_v2.exception.RiotException;
import com.gamegoo.gamegoo_v2.exception.common.ErrorCode;
import com.gamegoo.gamegoo_v2.riot.dto.RiotMatchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RiotRecordService {

    private final RestTemplate restTemplate;

    @Value("${spring.riot.api.key}")
    private String riotAPIKey;

    private static final String RIOT_MATCH_API_URL_TEMPLATE = "https://asia.api.riotgames" +
            ".com/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=%s&api_key=%s";
    private static final String RIOT_MATCH_INFO_API_URL_TEMPLATE = "https://asia.api.riotgames" +
            ".com/lol/match/v5/matches/%s?api_key=%s";

    /**
     * Riot API : 최근 선호 챔피언 3개 리스트 조회
     *
     * @param gameName
     * @param puuid
     * @return
     */
    @Transactional
    public List<Integer> getPreferChampionfromMatch(String gameName, String puuid) {
        // 1. 최근 플레이한 챔피언 리스트 조회
        List<Integer> recentChampionIds = null;
        int count = 20;

        //
        try {
            while ((recentChampionIds == null || recentChampionIds.size() < 3) && count <= 100) {
                List<String> recentMatchIds = getRecentMatchIds(puuid, count);

                recentChampionIds = recentMatchIds.stream()
                        .map(matchId -> getChampionIdFromMatch(matchId, gameName))
                        .filter(championId -> championId < 1000)
                        .toList();

                if (recentChampionIds.size() < 3) { //TODO: 챔피언 하나만 했을 경우도 있음
                    count += 10;
                }
            }
        } catch (Exception e) {
            log.info("회원가입 - 최근 선호 챔피언 값을 불러올 수 없습니다.");
            throw e;
        }

        // 최근 선호 챔피언 수가 충분하지 않을 경우 에러 발생
        if (recentChampionIds.size() < 3) {
            throw new RiotException(ErrorCode.RIOT_INSUFFICIENT_MATCHES);
        }

        // 2. 해당 캐릭터 중 많이 사용한 캐릭터 세 개 저장하기
        //      (1) 챔피언 사용 빈도 계산
        Map<Integer, Long> championFrequency = recentChampionIds.stream()
                .collect(Collectors.groupingBy(championId -> championId, Collectors.counting()));

        //      (2) 빈도를 기준으로 정렬하여 상위 3개의 챔피언 추출
        List<Integer> top3Champions = championFrequency.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        if (top3Champions.isEmpty()) {
            throw new RiotException(ErrorCode.RIOT_INSUFFICIENT_MATCHES);
        }

        return top3Champions;
    }

    /**
     * RiotAPI : puuid로 최근 매칭 20개의 matchId 가져오기
     *
     * @param puuid
     * @param count
     * @return
     */
    private List<String> getRecentMatchIds(String puuid, int count) {
        // 최근 매칭 ID count 개수만큼 가져오기
        String matchUrl = String.format(RIOT_MATCH_API_URL_TEMPLATE, puuid, count, riotAPIKey);
        String[] matchIds = restTemplate.getForObject(matchUrl, String[].class);

        return Arrays.asList(Objects.requireNonNull(matchIds));
    }


    /**
     * RiotAPI : matchId로 선호 챔피언 데이터 조회
     *
     * @param matchId
     * @param gameName
     * @return
     */
    public Integer getChampionIdFromMatch(String matchId, String gameName) {
        // 매치 정보 가져오기
        String matchInfoUrl = String.format(RIOT_MATCH_INFO_API_URL_TEMPLATE, matchId, riotAPIKey);
        RiotMatchResponse response = restTemplate.getForObject(matchInfoUrl, RiotMatchResponse.class);

        if (response == null || response.getInfo() == null || response.getInfo().getParticipants() == null) {
            throw new RiotException(ErrorCode.RIOT_NOT_FOUND);
        }

        // 참가자 정보에서 gameName과 일치하는 사용자의 champion ID 찾기
        return response.getInfo().getParticipants().stream()
                .filter(participant -> gameName.equals(participant.getRiotIdGameName()))
                .map(RiotMatchResponse.ParticipantDTO::getChampionId)
                .findFirst()
                .orElseGet(() -> {
                    log.info("회원가입 - 최근 선호 챔피언 값 중 id가 없는 챔피언이 있습니다.");
                    return null;
                });
    }

}
