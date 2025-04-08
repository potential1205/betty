//package org.example.betty.domain.game.async;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
//import org.example.betty.external.game.scraper.GameResultScraper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class GameResultAsyncExecutor {
//
//    private final GameResultScraper gameResultScraper;
//
//    @Async
//    public void executeResultScraping(String gameId, int seleniumIndex) {
//        try {
//            gameResultScraper.initDriver(gameId, seleniumIndex);
//            PreVoteAnswer result = gameResultScraper.scrapeResult(gameId);
//            if (result != null) {
//                // Redis 저장 또는 DB 저장 로직
//                log.info("[게임 결과 저장] gameId: {}, result: {}", gameId, result);
//            }
//        } catch (Exception e) {
//            log.error("[게임 결과 크롤링 실패] gameId: {}, error: {}", gameId, e.getMessage(), e);
//        }
//    }
//}
