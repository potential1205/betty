//package org.example.betty.external.game.scraper;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
//import org.example.betty.external.game.scraper.common.BaseScraper;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@Component
//public class GameResultScraper extends BaseScraper {
//
//    @Value("${selenium.remote.urls}")
//    private List<String> seleniumUrls;
//
//    private final Map<String, WebDriver> driverMap = new ConcurrentHashMap<>();
//
//    public void initDriver(String gameId, int index) {
//        try {
//            WebDriver driver = createDriver(seleniumUrls.get(index));
//            driverMap.put(gameId, driver);
//            log.info("[{}] GameResult WebDriver 초기화 완료", gameId);
//        } catch (Exception e) {
//            log.error("[{}] GameResult WebDriver 초기화 실패: {}", gameId, e.getMessage(), e);
//        }
//    }
//
//    public PreVoteAnswer scrapeResult(String gameId) {
//        WebDriver driver = driverMap.get(gameId);
//        if (driver == null) {
//            log.warn("[{}] WebDriver 없음 → 경기 결과 크롤링 중단", gameId);
//            return null;
//        }
//
//        try {
//            driver.get("https://statiz.sporki.com/schedule/");
//
//            List<WebElement> gameLinks = driver.findElements(By.cssSelector(".games li a"));
//            for (WebElement game : gameLinks) {
//                List<WebElement> teams = game.findElements(By.cssSelector(".team"));
//                List<WebElement> scores = game.findElements(By.cssSelector(".score"));
//
//                if (teams.size() != 2 || scores.size() != 2) continue;
//
//                String team1 = teams.get(0).getText().trim();
//                String team2 = teams.get(1).getText().trim();
//                int score1 = Integer.parseInt(scores.get(0).getText().trim());
//                int score2 = Integer.parseInt(scores.get(1).getText().trim());
//
//                String winner = score1 > score2 ? team1 : team2;
//                String summaryUrl = game.getAttribute("href"); // /schedule/?m=summary&s_no=...
//                String fullUrl = "https://statiz.sporki.com" + summaryUrl;
//
//                driver.get(fullUrl);
//
//                WebElement mvpElement = driver.findElement(By.cssSelector("a[href*='/player/?m=playerinfo']"));
//                WebElement mvpTeamElement = mvpElement.findElement(By.xpath("../../..")).findElement(By.cssSelector("td:nth-child(2)"));
//
//                String mvp = mvpElement.getText().trim();
//                String mvpTeam = mvpTeamElement.getText().trim();
//
//                log.info("[{}] 결과: MVP={}, MVP팀={}, 승리팀={}", gameId, mvp, mvpTeam, winner);
//
//                return PreVoteAnswer.builder()
//                        .winner(winner)
//                        .mvp(mvp)
//                        .mvpTeam(mvpTeam)
//                        .build();
//            }
//
//            log.warn("[{}] 해당 경기 찾을 수 없음", gameId);
//            return null;
//
//        } catch (Exception e) {
//            log.error("[{}] 경기 결과 스크래핑 실패: {}", gameId, e.getMessage(), e);
//            return null;
//        } finally {
//            quitDriver(driver);
//            driverMap.remove(gameId);
//        }
//    }
//
//
//}
