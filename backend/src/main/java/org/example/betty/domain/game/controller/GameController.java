package org.example.betty.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.request.SubmitLiveVoteRequest;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.service.GameService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "home-controller", description = "홈화면 경기 정보 API")
public class GameController {

    private final GameService gameReadService;

    @Operation(summary = "전체 경기 일정 조회", description = "일일 전체 경기 일정을 조회합니다.")
    @GetMapping("/games")
    public ResponseEntity<List<GameInfoResponse>> getTodayGames(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        List<GameInfoResponse> schedules = gameReadService.getTodayGameSchedules(accessToken);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "경기 상세 조회", description = "경기 상세 정보를 조회합니다.")
    @GetMapping("/games/{gameId}")
    public ResponseEntity<GameInfoResponse> getGameById(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable Long gameId) {

        GameInfoResponse response = gameReadService.getGameInfoById(accessToken, gameId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "라인업 조회", description = "MVP 베팅을 위한 경기 라인업을 조회합니다.")
    @GetMapping("/games/{gameId}/lineup")
    public ResponseEntity<RedisGameLineup> getLineup(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable Long gameId) {
        RedisGameLineup lineup = gameReadService.getGameLineup(accessToken, gameId);
        return ResponseEntity.ok(lineup);
    }

    @Operation(summary = "경기 상태 조회", description = "경기의 현재 상태를 조회합니다.")
    @GetMapping("/games/{gameId}/status")
    public ResponseEntity<String> getGameStatus(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable Long gameId) {

        String status = gameReadService.getGameStatus(accessToken, gameId);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "라이브 투표 제출", description = "유저가 실시간 중계 중 라이브 투표를 제출합니다.")
    @PostMapping("/games/{gameId}/votes/live")
    public ResponseEntity<Void> submitLiveVote(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @RequestBody SubmitLiveVoteRequest request) {
        gameReadService.submitLiveVote(accessToken, request);
        return ResponseEntity.ok().build();
    }

}
