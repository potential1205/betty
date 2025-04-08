//package org.example.betty.external.game.scraper;
//
//import lombok.RequiredArgsConstructor;
//import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class GameResultTestRunner implements CommandLineRunner {
//
//    private final GameResultScraper gameResultScraper;
//
//    @Override
//    public void run(String... args) throws Exception {
//        String gameId = "samsung-kt";
//        int seleniumIndex = 0;
//
//        gameResultScraper.initDriver(gameId, seleniumIndex);
//        PreVoteAnswer reuslt = gameResultScraper.scrapeResult(gameId);
//        System.out.println(reuslt.toString());
//    }
//}
