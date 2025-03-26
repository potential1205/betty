//package org.example.betty.domain.game.service;
//
//import lombok.RequiredArgsConstructor;
//import org.example.betty.domain.game.dto.resp.GameResponse;
//import org.example.betty.domain.game.repository.GameRepository;
//import org.example.betty.exception.BusinessException;
//import org.example.betty.exception.ErrorCode;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class GameServiceImpl implements GameService {
//
//    private final GameRepository gameRepository;
//
//    @Override
//    public GameResponse findGameById(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
//
//        return GameResponse.of(game);
//    }
//}
