package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.example.betty.external.game.scraper.parser.GameRelayInfoParser;
import org.example.betty.external.game.scraper.parser.PlayerRelayInfoParser;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LiveRelayScraper extends BaseScraper {

    @Value("${selenium.remote.urls}")
    private List<String> seleniumUrls;

    private final Map<String, WebDriver> driverMap = new ConcurrentHashMap<>();

    public void initDriver(String gameId, int index) {
        try {
            WebDriver driver = createDriver(seleniumUrls.get(index));
            driverMap.put(gameId, driver);
            log.info("[{}] WebDriver 초기화 완료", gameId);
        } catch (Exception e) {
            log.error("[{}] WebDriver 초기화 실패: {}", gameId, e.getMessage(), e);
        }
    }

    public RedisGameRelay scrapeRelay(String gameId) {
        int maxRetries = 3;
        final long retryDelayMillis = 1000L;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
//                log.info("[{}] 중계 크롤링 시도 #{}", gameId, attempt);
                WebDriver driver = driverMap.get(gameId);
                if (driver == null) {
                    log.warn("[{}] WebDriver 인스턴스가 존재하지 않음 → 크롤링 중단", gameId);
                    return null;
                }

                String url = "https://m.sports.naver.com/game/" + gameId + "/relay";
                driver.get(url);

                try {
                    WebElement endMessage = driver.findElement(By.cssSelector(".RelayEnd_title__yZjvp"));
                    String endText = endMessage.getText();
                    if (endText.contains("경기가 종료되었습니다.")) {
                        log.info("[{}] 경기 종료 감지 → WebDriver 종료 및 크롤링 중단", gameId);

                        driver.quit();
                        driverMap.remove(gameId);
                        return null;
                    }

                } catch (NoSuchElementException ignored) {}

                RedisGameRelay relay = new RedisGameRelay();

                String fullInning = GameRelayInfoParser.extractInningInfo(driver);
                if (fullInning != null && fullInning.contains(" - ")) {
                    String[] parts = fullInning.split(" - ");
                    relay.setInning(parts[0].trim());
                    relay.setTeamAtBat(parts[1].trim());
                } else {
                    relay.setInning(fullInning);
                    relay.setTeamAtBat(null);
                }
                relay.setPitchResult(GameRelayInfoParser.extractPitchResult(driver));
                relay.setNextBatters(GameRelayInfoParser.extractNextBatterNames(driver));
                relay.setRunnerOnBase(GameRelayInfoParser.extractRunnerOnBase(driver));
                relay.setScore(GameRelayInfoParser.extractScore(driver));
                relay.setOutCount(GameRelayInfoParser.extractOutCount(driver));

                List<WebElement> players = driver.findElements(
                        By.cssSelector(".RelayList_player_area__2ur0q.RelayList_type_current__eUw25")
                );

                if (players.size() >= 2) {
                    relay.setPitcher(PlayerRelayInfoParser.extractPitcherInfo(players.get(0), driver, gameId));
                    relay.setBatter(PlayerRelayInfoParser.extractBatterInfo(players.get(1), driver, gameId));
                } else {
                    log.warn("[{}] 투수/타자 정보 부족. players.size() = {}", gameId, players.size());
                }

                List<WebElement> allPlayers = driver.findElements(By.cssSelector(".RelayList_player_area__2ur0q"));
                if (allPlayers.size() >= 3) {
                    relay.setPreviousBatter(PlayerRelayInfoParser.extractPreviousBatterInfo(allPlayers.get(2), driver, gameId));
                } else {
                    relay.setPreviousBatter(null);
                }

                return relay;

            } catch (WebDriverException e) {
                log.warn("[{}] WebDriver 오류 발생 (시도 {}): {}", gameId, attempt, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("[{}] WebDriver 오류로 최대 재시도 초과. 크롤링 실패", gameId, e);
                    return null;
                }
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            } catch (Exception e) {
                log.error("[{}] 중계 파싱 중 예외 발생: {}", gameId, e.getMessage(), e);
                return null;
            }
        }

        return null;
    }
}
