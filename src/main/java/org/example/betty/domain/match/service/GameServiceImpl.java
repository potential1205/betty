package org.example.betty.domain.match.service;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.match.Game;
import org.example.betty.domain.match.dto.resp.GameResponse;
import org.example.betty.domain.match.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    public GameResponse findMatchById(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("해당 id를 가진 Match를 찾을 수 없습니다." ));

        return GameResponse.of(game);
    }
}
