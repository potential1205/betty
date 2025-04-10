//package org.example.betty.external.game.scraper;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.betty.domain.game.dto.redis.preview.TeamComparisonDto;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class GamePreviewTestRunner implements CommandLineRunner {
//
//    private final GamePreviewScraper gamePreviewScraper;
//
//    @Override
//    public void run(String... args) throws Exception {
//        String gameId = "20250410HHOB02025";
//        int seleniumIndex = 0;
//
//        log.info("[Runner] 게임 프리뷰 크롤링 시작: {}", gameId);
//        TeamComparisonDto preview = gamePreviewScraper.scrapePreview(gameId, seleniumIndex);
//
//        if (preview != null) {
//            log.info("[Runner] 크롤링 결과:\n{}", preview);
//        } else {
//            log.warn("[Runner] 크롤링 결과 없음!");
//        }
//    }
//}
