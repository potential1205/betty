package org.example.betty.domain.display.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
}