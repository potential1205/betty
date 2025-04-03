package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameResultScraper extends BaseScraper {

    /**
     * 경기 종료 후 결과 크롤링 메서드
     * @param statizId Statiz 내부 고유 경기 ID (예: 20250025)
     */
    public void scrapeResult(String statizId) {
//        WebDriver driver = createDriver();

//        try {
//            String url = "https://statiz.sporki.com/schedule/?m=summary&s_no=" + statizId;
//            driver.get(url);
//
//        } catch (Exception e) {
//            handleException(e, "GameResultScraper - " + statizId);
//        } finally {
//            quitDriver(driver);
//        }
    }
}
