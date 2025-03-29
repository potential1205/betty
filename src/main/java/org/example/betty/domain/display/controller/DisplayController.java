package org.example.betty.domain.display.controller;

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
@RequestMapping("/display")
public class DisplayController {

    private final DisplayService displayService;

    @GetMapping
    public ResponseEntity<DisplayResponse> getDisplayList(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        List<Display> displayList = displayService.getAllDisplayList(accessToken);

        return ResponseEntity.ok()
                .body(DisplayResponse.of(displayList));
    }

    @GetMapping("/my")
    public ResponseEntity<MyDisplayResponse> getMyDisplayList(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        List<Display> myDisplayList = displayService.getAllMyDisplayList(accessToken);

        return ResponseEntity.ok()
                .body(MyDisplayResponse.of(myDisplayList));
    }
}