package org.example.betty.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameScheduleListResponse;
import org.example.betty.domain.game.service.GameReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "home-controller", description = "홈화면 경기 정보 API")
public class GameController {

    private final GameReadService gameReadService;

    @Operation(summary = "오늘 경기 일정 조회", description = "Redis 또는 DB에서 오늘 날짜 기준 경기 일정을 조회합니다.")
    @GetMapping("/games/today")
    public ResponseEntity<GameScheduleListResponse> getTodayGames() {
        List<RedisGameSchedule> schedules = gameReadService.getTodayGameSchedules();
        return ResponseEntity.ok(GameScheduleListResponse.of(schedules));
    }

}
