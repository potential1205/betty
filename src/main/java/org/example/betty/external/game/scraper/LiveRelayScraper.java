package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class LiveRelayScraper extends BaseScraper {

    /**
     * 실시간 중계 페이지에서 5초마다 크롤링하는 메서드
     * @param gameId 경기 고유 ID (예: 20250326WOHT02025)
     * @return 실시간 경기 정보 RedisGameRelay DTO
     */
    public RedisGameRelay scrapeRelay(String gameId) {
        String url = "https://m.sports.naver.com/game/" + gameId + "/relay";
        WebDriver driver = createDriver();

        RedisGameRelay redisGameRelay = new RedisGameRelay();

        try {
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 1. 이닝 정보 수집
            redisGameRelay.setInning(extractInningInfo(wait));

            // 2. 현재 투수/타자 정보 수집
            extractCurrentPlayers(driver, redisGameRelay, gameId);

            // 3. 현재 타자의 투구 결과 수집
            extractPitchResults(driver, redisGameRelay, gameId);

        } catch (Exception e) {
            handleException(e, "LiveRelayScraper - " + gameId);
        } finally {
            quitDriver(driver);
        }

        return redisGameRelay;
    }

    /**
     * 이닝 정보 추출
     */
    private String extractInningInfo(WebDriverWait wait) {
        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".OffenseTitle_title__1LKfI"))
        );
        return element.getText().trim();
    }

    /**
     * 투수 / 타자 정보 추출
     */
    private void extractCurrentPlayers(WebDriver driver, RedisGameRelay redisGameRelay, String gameId) {
        List<WebElement> players = driver.findElements(
                By.cssSelector(".RelayList_player_area__2ur0q.RelayList_type_current__eUw25")
        );

        String[] roles = {"투수", "타자"};
        List<String> playerInfoList = new ArrayList<>();

        for (int i = 0; i < Math.min(players.size(), 2); i++) {
            try {
                WebElement profile = players.get(i).findElement(By.cssSelector(".RelayList_profile_info__2n-fN"));
                String name = profile.findElement(By.cssSelector(".RelayList_name__1THfS")).getText().trim();
                String position = profile.findElement(By.cssSelector(".RelayList_position__M6m4z")).getText().trim();

                playerInfoList.add(name + " (" + position + ")");
            } catch (NoSuchElementException e) {
                log.warn("[{}] {} 정보 누락", gameId, roles[i]);
            }
        }

        if (!playerInfoList.isEmpty()) {
            if (roles[0].equals("투수")) {
                redisGameRelay.setPitcher(playerInfoList.get(0));
            }
            if (roles[1].equals("타자")) {
                redisGameRelay.setBatter(playerInfoList.get(1));
            }
        }
    }

    /**
     * 타자의 투구 결과 정보 추출
     */
    private void extractPitchResults(WebDriver driver, RedisGameRelay redisGameRelay, String gameId) {
        List<WebElement> pitchElements = driver.findElements(By.cssSelector(".RelayList_history_list__13Jzg li"));
        List<String> pitchTexts = new ArrayList<>();

        for (WebElement el : pitchElements) {
            try {
                List<WebElement> spans = el.findElements(By.tagName("span"));
                for (WebElement span : spans) {
                    String text = span.getText().trim();
                    if (!text.isEmpty()) {
                        pitchTexts.add(text);
                    }
                }
            } catch (Exception e) {
                log.warn("[{}] 투구 데이터 누락 발생", gameId);
            }
        }

        if (!pitchTexts.isEmpty()) {
            redisGameRelay.setPlayByPlay(pitchTexts);
        }
    }
}
