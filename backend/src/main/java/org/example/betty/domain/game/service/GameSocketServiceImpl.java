package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.response.GameInfoDto;
import org.example.betty.domain.game.dto.response.GameProblemDto;
import org.example.betty.domain.game.dto.redis.live.RedisGameProblem;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSocketServiceImpl implements GameSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendGameEvent(Long gameId, String inning, String score) {
        String destination = "/topic/game/" + gameId;

        GameInfoDto gameInfoDto = GameInfoDto.builder()
                .inning(inning)
                .score(score)
                .build();

        messagingTemplate.convertAndSend(destination, gameInfoDto);
        log.info("[소켓 전송] gameId={} | data={}", gameId, gameInfoDto, gameInfoDto);
    }

    @Override
    public void sendGameProblem(Long gameId, RedisGameProblem redisGameProblem) {
        String destination = "/topic/problem/" + gameId;

        GameProblemDto gameProblemDto = GameProblemDto.builder()
                .redisGameProblem(redisGameProblem)
                .build();

        messagingTemplate.convertAndSend(destination, gameProblemDto);
        log.info("[소켓 전송] gameId={} | data={}", gameId, gameProblemDto);
    }

}