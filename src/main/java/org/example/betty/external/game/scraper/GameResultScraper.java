package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GameResultScraper extends BaseScraper {

    @Value("${selenium.remote.urls}")
    private List<String> seleniumUrls;

    private final Map<String, WebDriver> driverMap = new ConcurrentHashMap<>();

    public void initDriver(String gameId, int index) {
        try {
            WebDriver driver = createDriver(seleniumUrls.get(index));
            driverMap.put(gameId, driver);
            log.info("[{}] GameResult WebDriver 초기화 완료", gameId);
        } catch (Exception e) {
            log.error("[{}] GameResult WebDriver 초기화 실패: {}", gameId, e.getMessage(), e);
        }
    }

    // 반환객체 PreVoteAnswer로 바꿔주기!!!!!!
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
//            // 오늘 날짜의 경기 리스트 찾기
//            List<WebElement> gameLinks = driver.findElements(By.cssSelector(".games li a"));
//            for (WebElement game : gameLinks) {
//                String html = game.getAttribute("outerHTML");
//                if (html.contains("NC") && html.contains("키움")) { // ← 여기는 gameId 기반 조건으로 바꾸면 좋아
//                    String href = game.getAttribute("href");
//                    driver.get("https://statiz.sporki.com" + href);
//                    break;
//                }
//            }
//
//            // MVP 정보 추출
//            WebElement mvpElement = driver.findElement(By.cssSelector("a[href*='/player/?m=playerinfo']"));
//            WebElement mvpTeamElement = mvpElement.findElement(By.xpath("../../..")).findElement(By.cssSelector("td:nth-child(2)")); // 예: 팀명 열
//            WebElement winnerElement = driver.findElement(By.cssSelector(".team span.score.lead")).findElement(By.xpath("..")); // 예: 승리팀 span.team
//
//            String mvp = mvpElement.getText();
//            String mvpTeam = mvpTeamElement.getText();
//            String winner = winnerElement.getText();
//
//            log.info("[{}] 경기 결과 → MVP: {}, MVP팀: {}, 승리팀: {}", gameId, mvp, mvpTeam, winner);
//
//            return PreVoteAnswer.builder()
//                    .winner(winner)
//                    .mvp(mvp)
//                    .mvpTeam(mvpTeam)
//                    .build();
//
//        } catch (Exception e) {
//            log.error("[{}] 경기 결과 스크래핑 실패: {}", gameId, e.getMessage(), e);
//            return null;
//        } finally {
//            quitDriver(driver);
//            driverMap.remove(gameId);
//        }
//    }

}
