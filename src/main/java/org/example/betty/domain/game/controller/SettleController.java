package org.example.betty.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.game.dto.request.*;
import org.example.betty.domain.game.service.GameSettleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/settle")
@RequiredArgsConstructor
public class SettleController {

    private final GameSettleService gameSettleService;

    @Operation(summary = "라이브 투표 정산", description = "라이브 투표를 정산합니다.")
    @PostMapping("/live")
    public ResponseEntity<SuccessResponse> liveVoteSettle(
            @RequestBody LiveVoteSettleRequest request) {

        gameSettleService.liveVoteSettle(request.getGameId(), request.getHomeTeamId(), request.getAwayTeamId());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @Operation(summary = "팀 사전 투표 정산 준비", description = "팀 사전 투표 정산 준비를 시작합니다.")
    @PostMapping("/pre/team/ready")
    public ResponseEntity<SuccessResponse> teamPreVoteSettleReady(
            @RequestBody TeamPreVoteSettleReadyRequest request) {

        gameSettleService.createPreVoteTeamSettle(request.getGameId(), request.getTeamAId(), request.getTeamBId(), request.getStartTime(),
                request.getTeamATokenAddress(), request.getTeamBTokenAddress());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @Operation(summary = "팀 사전 투표 정산", description = "팀 사전 투표를 정산합니다.")
    @PostMapping("/pre/team")
    public ResponseEntity<SuccessResponse> teamPreVoteSettle(
            @RequestBody TeamPreVoteSettleRequest request) {

        gameSettleService.preVoteTeamSettle(request.getGameId(), request.getWinningTeamId());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @Operation(summary = "MVP 사전 투표 정산 준비", description = "MVP 사전 투표 정산 준비를 시작합니다.")
    @PostMapping("/pre/mvp/ready")
    public ResponseEntity<SuccessResponse> mvpPreVoteSettleReady(
            @RequestBody MvpPreVoteSettleReadyRequest request) {

        gameSettleService.createPreVoteMVPSettle(
                request.getGameId(), request.getPlayerIds(), request.getTokenAddresses(), request.getStartTime());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @Operation(summary = "MVP 사전 투표 정산", description = "MVP 사전 투표를 정산합니다.")
    @PostMapping("/pre/mvp")
    public ResponseEntity<SuccessResponse> mvpPreVoteSettle(
            @RequestBody MvpPreVoteSettleRequest request) {

        gameSettleService.preVoteMVPSettle(request.getGameId(), request.getWinningPlayerId());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }
}
