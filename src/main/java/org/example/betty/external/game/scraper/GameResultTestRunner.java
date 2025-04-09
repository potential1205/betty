//package org.example.betty.external.game.scraper;
//
//import lombok.RequiredArgsConstructor;
//import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
//import org.example.betty.domain.game.service.GameService;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class GameResultTestRunner implements CommandLineRunner {
//
//    private final GameResultScraper gameResultScraper;
//    private final GameService gameService;
//
//    @Override
//    public void run(String... args) {
//        String gameId = "20250408LGWO02025";  // 원하는 gameId로 교체
//        int seleniumIndex = 0;
//
//        try {
//            System.out.println("====== [START 테스트 실행] ======");
//
//            Long gameDbId = gameService.resolveGameDbId(gameId);
//            System.out.println("✅ gameDbId: " + gameDbId);
//
//            Map<String, Long> teamIds = gameService.resolveTeamIdsFromGameId(gameId);
//            System.out.println("✅ homeTeamId: " + teamIds.get("homeTeamId") + "awayTeamId: " + teamIds.get("awayTeamId"));
//
//            gameResultScraper.initDriver(gameId, seleniumIndex);
//            PreVoteAnswer result = gameResultScraper.scrapeResult(gameId);
//            System.out.println("✅ PreVoteAnswer: " + result);
//
//            System.out.println("====== [END 테스트 성공] ======");
//
//        } catch (Exception e) {
//            System.out.println("❌ 테스트 실행 중 예외 발생!");
//            e.printStackTrace();
//        }
//    }
//}
