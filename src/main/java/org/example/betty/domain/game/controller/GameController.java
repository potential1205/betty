package org.example.betty.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameScheduleListResponse;
import org.example.betty.domain.game.service.GameService;
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

    private final GameService gameReadService;
    private final SseService sseService;

    @Operation(summary = "전체 경기 일정 조회", description = "일일 전체 경기 일정을 조회합니다.")
    @GetMapping("/games")
    public ResponseEntity<GameScheduleListResponse> getTodayGames() {
        List<RedisGameSchedule> schedules = gameReadService.getTodayGameSchedules();
        return ResponseEntity.ok(GameScheduleListResponse.of(schedules));
    }

    @Operation(summary = "라인업 조회", description = "MVP 베팅을 위한 경기 라인업을 조회합니다.")
    @GetMapping("/games/{gameId}/lineup")
    public ResponseEntity<RedisGameLineup> getLineup(@PathVariable String gameId) {
        RedisGameLineup lineup = gameReadService.getGameLineup(gameId);
        return ResponseEntity.ok(lineup);
    }

    @Operation(summary = "경기 상태 조회", description = "경기의 현재 상태를 조회합니다.")
    @GetMapping("/games/{gameId}/status")
    public ResponseEntity<String> getGameStatus(@PathVariable String gameId) {
        String status = gameReadService.getGameStatus(gameId);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "SSE 실시간 스트리밍 요청", description = "실시간 경기 데이터 스트리밍을 요청합니다.")
    @GetMapping(value = "/games/{gameId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String gameId) {
        return sseService.stream(gameId);
    }



}
