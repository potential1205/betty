package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameReadServiceImpl implements GameReadService {

    private final GameRepository gamesRepository;

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    private static final String REDIS_GAME_PREFIX = "games:";

    @Override
    public List<RedisGameSchedule> getTodayGameSchedules() {
        // 변경해주기!!!!!!
//        LocalDate today = LocalDate.now();
        LocalDate today = LocalDate.now().minusDays(1);

        List<Game> games = gamesRepository.findByGameDate(today);
        List<RedisGameSchedule> schedules = new ArrayList<>();

        for (Game game : games) {
            String gameId = generateGameId(game);
            RedisGameSchedule schedule = RedisGameSchedule.builder()
                    .gameId(gameId)
                    .season(game.getSeason())
                    .gameDate(game.getGameDate().toString())
                    .startTime(game.getStartTime().toString())
                    .stadium(game.getStadium())
                    .homeTeam(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeam(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();

            schedules.add(schedule);
        }
        return schedules;
    }

    @Override
    public RedisGameLineup getGameLineup(String gameId) {
        String gameDate = gameId.substring(0, 4) + "-" + gameId.substring(4, 6) + "-" + gameId.substring(6, 8);
        String redisKey = REDIS_GAME_PREFIX + gameDate + ":" + gameId;

        HashOperations<String, String, Object> hashOps = redisTemplate2.opsForHash();
        Object rawLineup = hashOps.get(redisKey, "lineup");

        if (rawLineup == null) {
            log.warn("[GameReadService] 라인업 정보 없음 - key: {}, field: lineup", redisKey);
            throw new BusinessException(ErrorCode.NOT_FOUND_LINEUP);
        }

        if (!(rawLineup instanceof RedisGameLineup)) {
            log.error("[GameReadService] 라인업 타입 불일치 - 실제 타입: {}", rawLineup.getClass());
            throw new BusinessException(ErrorCode.INVALID_REDIS_DATA);
        }

        log.info("[GameReadService] 라인업 조회 성공 - gameId: {}", gameId);
        return (RedisGameLineup) rawLineup;
    }



    private String generateGameId(Game game) {
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }

}
