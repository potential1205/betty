package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;
    private final TeamRepository teamRepository;


    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    private static final String REDIS_GAME_PREFIX = "games:";

    @Override
    public List<GameInfoResponse> getTodayGameSchedules(String accessToken) {

        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        LocalDate today = LocalDate.now();
        List<Game> games = gameRepository.findByGameDate(today);
        List<GameInfoResponse> schedules = new ArrayList<>();

        for (Game game : games) {
            GameInfoResponse schedule = GameInfoResponse.builder()
                    .gameId(game.getId())
                    .homeTeamId(game.getHomeTeam().getId())
                    .awayTeamId(game.getAwayTeam().getId())
                    .season(game.getSeason())
                    .gameDate(game.getGameDate().toString())
                    .startTime(game.getStartTime().toString())
                    .stadium(game.getStadium())
                    .homeTeamName(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeamName(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();

            schedules.add(schedule);
        }
        return schedules;
    }

    @Override
    public RedisGameLineup getGameLineup(String accessToken, Long gameId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if (optionalGame.isEmpty()) {
            log.warn("[Game 없음] gameId: {}", gameId);
            return null;
        }

        Game game = optionalGame.get();
        LocalDate gameDate = game.getGameDate();

        String redisKey = REDIS_GAME_PREFIX + gameDate + ":" + generateGameCode(game);

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


    @Override
    @Transactional
    public void updateGameStatusToLive(Game game) {
        game.setStatus("LIVE");
        gameRepository.save(game);
        log.info("[경기 상태 변경] gameId={} → LIVE", game.getId());
    }

    @Override
    @Transactional
    public void updateGameStatusToEnded(Game game) {
        game.setStatus("ENDED");
        gameRepository.save(game);
        log.info("[경기 상태 변경] gameId={} → ENDED", game.getId());
    }

    @Override
    public Game findGameByGameId(String gameId) {
        String gameDateStr = gameId.substring(0, 8);
        String awayTeamCode = gameId.substring(8, 10);
        String homeTeamCode = gameId.substring(10, 12);
        String seasonStr = gameId.substring(13); // "02025" 형태

        if (seasonStr.startsWith("0")) {
            seasonStr = seasonStr.substring(1); // "02025" → "2025"
        }

        int season = Integer.parseInt(seasonStr);

        LocalDate gameDate = LocalDate.parse(gameDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

        return gameRepository.findByGameDateAndSeasonAndHomeTeam_TeamCodeAndAwayTeam_TeamCode(
                gameDate, season, homeTeamCode, awayTeamCode
        ).orElseThrow(() -> new RuntimeException("gameId로 Game 조회 실패: " + gameId));
    }

    @Override
    public String getGameStatus(String accessToken, Long gameId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if (optionalGame.isEmpty()) {
            log.warn("[Game 없음] gameId: {}", gameId);
            return null;
        }

        Game game = optionalGame.get();
        return game.getStatus();
    }

    @Override
    public Map<String, Long> resolveTeamIdsFromGameId(String gameId) {
        String awayCode = gameId.substring(8, 10);
        String homeCode = gameId.substring(10, 12);

        Long awayTeamId = teamRepository.findByTeamCode(awayCode).get().getId();  // 예외 처리 없음
        Long homeTeamId = teamRepository.findByTeamCode(homeCode).get().getId();

        return Map.of(
                "awayTeamId", awayTeamId,
                "homeTeamId", homeTeamId
        );
    }

    @Override
    public Long resolveGameDbId(String gameId) {
        int season = 2000 + Integer.parseInt(gameId.substring(0, 2));
        int month = Integer.parseInt(gameId.substring(2, 4));
        int day = Integer.parseInt(gameId.substring(4, 6));

        String awayCode = gameId.substring(6, 8);
        String homeCode = gameId.substring(8, 10);

        LocalDate gameDate = LocalDate.of(season, month, day);

        return gameRepository
                .findByGameDateAndSeasonAndHomeTeam_TeamCodeAndAwayTeam_TeamCode(
                        gameDate, season, homeCode, awayCode
                )
                .get()
                .getId();
    }

    private String generateGameCode(Game game) {
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }





}
