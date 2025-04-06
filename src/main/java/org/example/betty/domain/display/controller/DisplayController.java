package org.example.betty.domain.display.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.display.dto.CreateDisplayAccessRequest;
import org.example.betty.domain.display.dto.DisplayResponse;
import org.example.betty.domain.display.dto.MyDisplayResponse;
import org.example.betty.domain.display.entity.Display;
import org.example.betty.domain.display.service.DisplayService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/display")
@Slf4j
public class DisplayController {

    private final DisplayService displayService;

    @Operation(summary = "전체 전광판 목록 조회", description = "전체 전광판 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<DisplayResponse> getDisplayList(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        List<Display> displayList = displayService.getAllDisplayList(accessToken);

        return ResponseEntity.ok()
                .body(DisplayResponse.of(displayList));
    }

    @Operation(summary = "내 전광판 목록 조회", description = "사용자가 토큰을 지불한 전광판 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<MyDisplayResponse> getMyDisplayList(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        List<Display> myDisplayList = displayService.getAllMyDisplayList(accessToken);

        return ResponseEntity.ok()
                .body(MyDisplayResponse.of(myDisplayList));
    }

    @Operation(summary = "전광판 접근 권한 조회", description = "사용자가 전광판에 접근할 수 있는지 확인합니다.")
    @GetMapping("/games/{gameId}/teams/{teamId}/access")
    public ResponseEntity<SuccessResponse> checkDisplayAccess(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable Long gameId, @PathVariable Long teamId) {

        displayService.checkDisplayAccess(accessToken, gameId, teamId);

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @Operation(summary = "전광판 접근 권한 생성", description = "사용자의 전광판 접근 권한을 생성합니다.")
    @PostMapping("/games/{gameId}/teams/{teamId}/access")
    public ResponseEntity<SuccessResponse> crateDisplayAccess(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable Long gameId, @PathVariable Long teamId,
            @RequestBody CreateDisplayAccessRequest request) {

        displayService.createDisplayAccess(accessToken, gameId, teamId, request.getTxHash());

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    // 테스트용 임시 API
    @Operation(summary = "게임종료", description = "게임이 종료되어 전광판을 이미지로 저장합니다.")
    @PostMapping("/games/{gameId}/teams/{teamId}/end")
    public ResponseEntity<SuccessResponse> gameEnd(
            @PathVariable Long gameId, @PathVariable Long teamId,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        displayService.gameEnd(accessToken, gameId, teamId);

        return ResponseEntity.ok()
                .body(SuccessResponse.of(true));
    }

    // 테스트용 임시 API
    @Operation(summary = "이닝종료", description = "이닝이 종료되어 전광판을 이미지로 저장합니다.")
    @PostMapping("/games/{gameId}/teams/{teamId}/inning/{inning}/end")
    public ResponseEntity<SuccessResponse> inningEnd(
            @PathVariable Long gameId, @PathVariable Long teamId, @PathVariable int inning,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        displayService.inningEnd(accessToken, gameId, teamId, inning);

        return ResponseEntity.ok()
                .body(SuccessResponse.of(true));
    }
}