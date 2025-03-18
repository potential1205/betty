package org.example.betty.domain.game.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.game.dto.resp.GameResponse;
import org.example.betty.domain.game.service.GameService;
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
