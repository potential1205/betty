package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.repository.GamesRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static org.example.betty.domain.game.service.GameCacheServiceImpl.REDIS_GAME_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameReadServiceImpl implements GameReadService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final GamesRepository gamesRepository;

    @Override
    public List<RedisGameSchedule> getTodayGameSchedules() {
        LocalDate today = LocalDate.now();
        String pattern = REDIS_GAME_PREFIX + today + ":*";

        Set<String> redisKeys = redisTemplate.keys(pattern);
        List<RedisGameSchedule> schedules = new ArrayList<>();
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        if (redisKeys != null && !redisKeys.isEmpty()) {
            for (String key : redisKeys) {
                RedisGameSchedule gameInfo = (RedisGameSchedule) hashOps.get(key, "gameInfo");
                if (gameInfo != null) {
                    schedules.add(gameInfo);
                }
            }
            log.info("[GameReadService] Redis에서 {}개 경기 일정 조회", schedules.size());
            return schedules;
        }

        // fallback: DB에서 조회
        List<Game> games = gamesRepository.findByGameDate(today);
        for (Game game : games) {
            RedisGameSchedule schedule = RedisGameSchedule.builder()
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
        log.warn("[GameReadService] Redis에 없음 → DB에서 {}개 경기 일정 조회", schedules.size());
        return schedules;
    }
}
