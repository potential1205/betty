package org.example.betty.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.dto.response.GameScheduleListResponse;
import org.example.betty.domain.game.service.GameReadService;
import org.example.betty.domain.game.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "home-controller", description = "홈화면 경기 정보 API")
public class GameController {

    private final GameReadService gameReadService;
    private final SseService sseService;

    @Operation(summary = "오늘 경기 일정 조회", description = "전체 경기 일정을 조회합니다.")
    @GetMapping("/games")
    public ResponseEntity<GameScheduleListResponse> getTodayGames() {
        List<RedisGameSchedule> schedules = gameReadService.getTodayGameSchedules();
        return ResponseEntity.ok(GameScheduleListResponse.of(schedules));
    }



    @Operation(summary = "라인업 조회", description = "경기 라인업을 조회합니다.")
    @PostMapping("/games/{gameId}/lineup")
    public ResponseEntity<RedisGameLineup> getLineup(@PathVariable String gameId) {
        RedisGameLineup lineup = gameReadService.getGameLineup(gameId);
        return ResponseEntity.ok(lineup);
    }

    @GetMapping(value = "/subscribe/{gameId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String gameId) {
        return sseService.subscribe(gameId);
    }
}
