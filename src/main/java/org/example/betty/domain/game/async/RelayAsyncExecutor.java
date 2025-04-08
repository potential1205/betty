package org.example.betty.domain.game.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.service.GameRelayEventHandler;
import org.example.betty.domain.game.service.GameService;
import org.example.betty.domain.game.service.SseService;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayAsyncExecutor {

    private final LiveRelayScraper liveRelayScraper;
    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> relayTasks = new ConcurrentHashMap<>();
    private final GameRelayEventHandler gameRelayEventHandler;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final GameService gameService;
    private final SseService sseService;

    //실시간 중계 크롤링을 5초 간격으로 실행하는 메서드
    @Async
    public void startRelay(String gameId, int seleniumIndex) {
        liveRelayScraper.initDriver(gameId, seleniumIndex);

        Runnable task = () -> {
            try {
                RedisGameRelay relayData = liveRelayScraper.scrapeRelay(gameId);

                if (relayData == null) {
                    stopRelay(gameId);
                    log.info("[중계 중단] gameId: {} - 경기 종료 감지로 반복 크롤링 중단", gameId);
                    
                    // 승리팀 저장
//                    RedisGameRelay lastRelay = getLastRelayFromRedis(gameId);
//                    if (lastRelay != null && lastRelay.getScore() != null) {
//                        saveWinningTeamToRedis(gameId, lastRelay.getScore());
//                    }
                    
                    // 경기 종료 상태 업데이트
                    Game game = gameService.findGameByGameId(gameId);
                    gameService.updateGameStatusToEnded(game);
                    sseService.send(gameId, "ENDED");

                    return;
                }

                saveRelayDataToRedis(gameId, relayData);
                log.info("[중계 크롤링] gameId: {} - 크롤링 완료", gameId);
                gameRelayEventHandler.handleRelayUpdate(gameId, relayData);
                gameRelayEventHandler.handleGameInfoChange(gameId, relayData);

            } catch (Exception e) {
                log.error("[중계 크롤링 실패] gameId: {}", gameId, e);
            }
        };

        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, new Date(), 5000L);
        relayTasks.put(gameId, future);
    }

    public void stopRelay(String gameId) {
        ScheduledFuture<?> future = relayTasks.get(gameId);
        if (future != null) {
            future.cancel(false);
            relayTasks.remove(gameId);
            log.info("[중계 중단] gameId: {} - 5초 반복 크롤링 종료", gameId);
        }
    }

    private void saveRelayDataToRedis(String gameId, RedisGameRelay relayData) {
        String redisKey = "games:" + LocalDate.now() + ":" + gameId;
        HashOperations<String, String, Object> hashOps = redisTemplate2.opsForHash();
        hashOps.put(redisKey, "relay", relayData);

        log.info("[중계 저장] gameId: {} - Redis 저장 완료", gameId);
    }

//    private RedisGameRelay getLastRelayFromRedis(String gameId) {
//        String redisKey = "games:" + LocalDate.now() + ":" + gameId;
//        Object obj = redisTemplate2.opsForHash().get(redisKey, "relay");
//        if (obj instanceof RedisGameRelay) {
//            return (RedisGameRelay) obj;
//        }
//        return null;
//    }

//    public void saveWinningTeamToRedis(String gameId, String score) {
//        try {
//            String[] parts = score.split(":");
//            String[] left = parts[0].trim().split(" ");
//            String[] right = parts[1].trim().split(" ");
//
//            String team1 = left[0];
//            int score1 = Integer.parseInt(left[1]);
//            String team2 = right[0];
//            int score2 = Integer.parseInt(right[1]);
//
//            String winningTeam;
//            if (score1 > score2) {
//                winningTeam = team1;
//            } else if (score2 > score1) {
//                winningTeam = team2;
//            } else {
//                winningTeam = "무승부";
//            }
//
//            String key = "prevote:result:" + gameId;
//            HashOperations<String, String, String> hashOps = redisTemplate2.opsForHash();
//
//            hashOps.put(key, "winningTeam", winningTeam);
//
//            log.info("[승리팀 저장] gameId: {}, team: {}", gameId, winningTeam);
//
//        } catch (Exception e) {
//            log.error("[승리팀 저장 실패] gameId: {}, score: {}", gameId, score, e);
//        }
//    }




}
