package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.service.GameServiceImpl;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LineupScraper {


    /**
     * 당일 경기 라인업 페이지 크롤러
     */
    public void scrapeLineup(String gameId) {
        String url = "https://m.sports.naver.com/game/" + gameId + "/relay";
        String driverPath = System.getProperty("user.dir") + "/src/main/resources/chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);
            Thread.sleep(2000);

            // TODO: 라인업 데이터 추출 로직 작성
            log.info("라인업 중계 페이지 접속 완료: {}", url);

        } catch (Exception e) {
            log.error("라인업 페이지 크롤링 실패: {}", e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
