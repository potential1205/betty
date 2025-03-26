//package org.example.betty.domain.game.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.betty.domain.game.dto.resp.GameResponse;
//import org.example.betty.domain.game.service.GameService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/game")
//@RequiredArgsConstructor
//public class GameController {
//
//    private final GameService gameService;
//
//    @GetMapping("/{gameId}")
//    public ResponseEntity<GameResponse> getGameById(@PathVariable("gameId") Long gameId) {
//        GameResponse gameResponse = gameService.findGameById(gameId);
//
//        return ResponseEntity.ok(gameResponse);
//    }
//}
