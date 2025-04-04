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
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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

    public RedisGameRelay scrapeRelay(String gameId, int index) {
        RedisGameRelay relay = new RedisGameRelay();

        if (!driverMap.containsKey(gameId)) {
            log.warn("[{}] 중계 스킵: WebDriver가 종료되어 없음", gameId);
            return null;
        }

        WebDriver driver = driverMap.get(gameId);
        if (driver == null) {
            log.error("[{}] WebDriver가 null입니다 → 크롤링 중단", gameId);
            return null;
        }

        try {
            String url = "https://m.sports.naver.com/game/" + gameId + "/relay";
            driver.get(url);

            // 기본 정보 파싱
            String fullInning = GameRelayInfoParser.extractInningInfo(driver);  // 예: "5회말 - 삼성공격"
            if (fullInning != null && fullInning.contains(" - ")) {
                String[] parts = fullInning.split(" - ");
                relay.setInning(parts[0].trim());      // 예: "5회말"
                relay.setTeamAtBat(parts[1].trim());   // 예: "삼성공격"
            } else {
                relay.setInning(fullInning);
                relay.setTeamAtBat(null); // 또는 "" 처리
            }


            relay.setPitchResult(GameRelayInfoParser.extractPitchResult(driver));
            relay.setNextBatters(GameRelayInfoParser.extractNextBatterNames(driver));
            relay.setRunnerOnBase(GameRelayInfoParser.extractRunnerOnBase(driver));
            relay.setScore(GameRelayInfoParser.extractScore(driver));
            relay.setOutCount(GameRelayInfoParser.extractOutCount(driver));




            // 현재 투수 / 타자
            List<WebElement> players = driver.findElements(
                    By.cssSelector(".RelayList_player_area__2ur0q.RelayList_type_current__eUw25")
            );

            if (players.size() >= 2) {
                WebElement pitcherBox = players.get(0);
                WebElement batterBox = players.get(1);
                relay.setPitcher(PlayerRelayInfoParser.extractPitcherInfo(pitcherBox, driver, gameId));
                relay.setBatter(PlayerRelayInfoParser.extractBatterInfo(batterBox, driver, gameId));
            } else {
                log.warn("[{}] 투수/타자 박스를 찾을 수 없습니다. players.size() = {}", gameId, players.size());
            }

            // 직전 타자
            List<WebElement> allPlayers = driver.findElements(By.cssSelector(".RelayList_player_area__2ur0q"));
            if (allPlayers.size() >= 3) {
                WebElement previousBatterBox = allPlayers.get(2);
                relay.setPreviousBatter(PlayerRelayInfoParser.extractPreviousBatterInfo(previousBatterBox, driver, gameId));
            } else {
                relay.setPreviousBatter(null);
            }

        } catch (Exception e) {
            log.error("[중계 크롤링 실패] - gameId: {}, 이유: {}", gameId, e.getMessage(), e);
            return null;
        }

        return relay;
    }
}
