package org.example.betty.domain.match.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.match.dto.resp.GameResponse;
import org.example.betty.domain.match.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getMatchById(@PathVariable("gameId") Long gameId) {
        GameResponse gameResponse = gameService.findMatchById(gameId);

        return ResponseEntity.ok(gameResponse);
    }
}
