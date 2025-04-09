package org.example.betty.domain.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.game.dto.request.LiveVoteSettleRequest;
import org.example.betty.domain.game.dto.request.PreVoteSettleRequest;
import org.example.betty.domain.game.dto.request.PreVoteSettleCreateRequest;
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

    @Operation(summary = "사전 투표 정산 준비", description = "사전 투표 정산 준비를 시작합니다.")
    @PostMapping("/pre/create")
    public ResponseEntity<SuccessResponse> preVoteSettleReady(
            @RequestBody PreVoteSettleCreateRequest request) {

        gameSettleService.createGame(request.getGameId(), request.getTeamAId(), request.getTeamBId(), request.getStartTime(),
                request.getTeamATokenAddress(), request.getTeamBTokenAddres());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @Operation(summary = "사전 투표 정산", description = "사전 투표를 정산합니다.")
    @PostMapping("/pre")
    public ResponseEntity<SuccessResponse> preVoteSettle(
            @RequestBody PreVoteSettleRequest request) {

        gameSettleService.preVoteSettle(request.getGameId(), request.getWinningTeamId());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }
}
