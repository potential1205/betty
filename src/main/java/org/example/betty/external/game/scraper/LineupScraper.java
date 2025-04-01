package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

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

        } catch (Exception e) {
            handleException(e, "LineupScraper - " + gameId);
        } finally {
            quitDriver(driver);
        }
    }
}
