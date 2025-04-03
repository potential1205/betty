package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component
public class LiveRelayScraper extends BaseScraper {

    @Value("${selenium.remote.urls}")
    private List<String> seleniumUrls;

    private final Map<String, WebDriver> driverMap = new ConcurrentHashMap<>();

    /**
     * 실시간 중계 페이지에서 5초마다 크롤링하는 메서드
     * @param gameId 경기 고유 ID (예: 20250326WOHT02025)
     * @return 실시간 경기 정보 RedisGameRelay DTO
     */
    public RedisGameRelay scrapeRelay(String gameId, int index) {
        RedisGameRelay relay = new RedisGameRelay();

        WebDriver driver = driverMap.computeIfAbsent(gameId, id -> createDriver(seleniumUrls.get(index)));

        if (driver == null) {
            log.error("[{}] WebDriver 생성 실패 → 중계 크롤링 중단", gameId);
            return null;
        }

        try {
            String url = "https://m.sports.naver.com/game/" + gameId + "/relay";
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            extractInningInfo(wait, relay);
            extractCurrentPlayers(driver, relay, gameId);
            extractPitchResults(driver, relay, gameId);

        } catch (Exception e) {
            log.error("[중계 크롤링 실패] - gameId: {}, 이유: {}", gameId, e.getMessage(), e);
            return null;
        }

        return relay;
    }

    // 다른곳에서 사용예정
    public void stopRelay(String gameId) {
        WebDriver driver = driverMap.remove(gameId);
        if (driver != null) {
            try {
                driver.quit();
                log.info("[{}] 중계 종료 - WebDriver 정상 종료", gameId);
            } catch (Exception e) {
                log.warn("[{}] WebDriver 종료 실패: {}", gameId, e.getMessage());
            }
        }
    }

    private void extractInningInfo(WebDriverWait wait, RedisGameRelay relay) {
        try {
            WebElement element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".OffenseTitle_title__1LKfI"))
            );
            relay.setInning(element.getText().trim());
        } catch (TimeoutException e) {
            log.warn("[이닝 정보 없음] 중계 페이지 로딩 지연 또는 종료된 경기");
        }
    }

    private void extractCurrentPlayers(WebDriver driver, RedisGameRelay relay, String gameId) {
        try {
            List<WebElement> players = driver.findElements(
                    By.cssSelector(".RelayList_player_area__2ur0q.RelayList_type_current__eUw25")
            );

            if (players.size() >= 2) {
                WebElement pitcher = players.get(0).findElement(By.cssSelector(".RelayList_profile_info__2n-fN"));
                relay.setPitcher(getPlayerInfo(pitcher));
                WebElement batter = players.get(1).findElement(By.cssSelector(".RelayList_profile_info__2n-fN"));
                relay.setBatter(getPlayerInfo(batter));
            }
        } catch (Exception e) {
            log.warn("[{}] 투수/타자 정보 파싱 실패: {}", gameId, e.getMessage());
        }
    }

    private String getPlayerInfo(WebElement profile) {
        try {
            String name = profile.findElement(By.cssSelector(".RelayList_name__1THfS")).getText().trim();
            String pos = profile.findElement(By.cssSelector(".RelayList_position__M6m4z")).getText().trim();
            return name + " (" + pos + ")";
        } catch (Exception e) {
            return "정보 없음";
        }
    }

    private void extractPitchResults(WebDriver driver, RedisGameRelay relay, String gameId) {
        try {
            List<WebElement> pitchElements = driver.findElements(By.cssSelector(".RelayList_history_list__13Jzg li"));
            List<String> pitches = new ArrayList<>();

            for (WebElement el : pitchElements) {
                el.findElements(By.tagName("span")).forEach(span -> {
                    String text = span.getText().trim();
                    if (!text.isEmpty()) pitches.add(text);
                });
            }

            if (!pitches.isEmpty()) {
                relay.setPlayByPlay(pitches);
            }
        } catch (Exception e) {
            log.warn("[{}] 투구 결과 파싱 실패: {}", gameId, e.getMessage());
        }
    }
}
