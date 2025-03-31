package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LineupScraper extends BaseScraper {

    /**
     * 당일 경기 라인업 페이지 크롤러
     * @param gameId 경기 고유 ID (예: 20250326WOHT02025)
     */
    public void scrapeLineup(String gameId) {
        WebDriver driver = createDriver();

        try {
            String url = "https://m.sports.naver.com/game/" + gameId + "/lineup";
            driver.get(url);

            // 1. 상위 wrapper에서 시작
            WebElement lineupWrapper = driver.findElement(By.cssSelector(".lineup_group"));

            List<WebElement> teamSections = lineupWrapper.findElements(
                    By.cssSelector(".Lineup_lineup_area__2aNOv .Lineup_lineup_list__1_CNQ")
            );

            System.out.println("▶ 라인업 영역 수: " + teamSections.size());


//            if (teamSections.size() < 2) {
//                log.warn("[LineupScraper] 라인업 요소 부족 ({}개) → gameId: {}", teamSections.size(), gameId);
//                return;
//            }
//
//            // 3. 원정팀 (왼쪽) / 홈팀 (오른쪽)
//            WebElement awaySection = teamSections.get(0);
//            WebElement homeSection = teamSections.get(1);
//
//            System.out.println("\n▶ 원정팀");
//            printPlayers(awaySection);
//
//            System.out.println("\n▶ 홈팀");
//            printPlayers(homeSection);

        } catch (Exception e) {
            handleException(e, "LineupScraper - " + gameId);
        } finally {
            quitDriver(driver);
        }
    }



    // 선수 정보 출력
    private void printPlayers(WebElement teamElement) {
        List<WebElement> playerItems = teamElement.findElements(By.cssSelector(".Lineup_lineup_item__32s4M"));

        for (WebElement item : playerItems) {
            String name = item.findElement(By.cssSelector(".lineup_name__jY19m")).getText();
            String position = item.findElement(By.cssSelector(".lineup_position__265hb")).getText();
            System.out.println("- " + name + " (" + position + ")");
        }
    }
}
