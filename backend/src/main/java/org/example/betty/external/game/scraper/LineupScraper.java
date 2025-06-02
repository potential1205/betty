package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerInfo;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.TeamLineup;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class LineupScraper extends BaseScraper {

    @Value("${selenium.remote.urls}")
    private List<String> seleniumUrls;

    public RedisGameLineup scrapeLineup(String gameId, int seleniumIndex) {
        String remoteUrl = seleniumUrls.get(seleniumIndex);
        WebDriver driver1 = createDriver(remoteUrl);
        WebDriver driver2 = createDriver(remoteUrl);

        if (driver1 == null || driver2 == null) {
            log.error("[{}] WebDriver 생성 실패 → 라인업 크롤링 중단", gameId);
            quitDriver(driver1);
            quitDriver(driver2);
            return null;
        }

        try {
            String url = "https://m.sports.naver.com/game/" + gameId + "/lineup";
            driver1.get(url);

            try {
                new WebDriverWait(driver1, Duration.ofSeconds(30))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".lineup_group")));
            } catch (Exception e) {
                log.error("[{}] lineup_group 요소 로딩 대기 실패: {}", gameId, e.getMessage(), e);
                return null;
            }

            WebElement lineupWrapper;
            try {
                lineupWrapper = driver1.findElement(By.cssSelector(".lineup_group"));
            } catch (NoSuchElementException e) {
                log.error("[{}] lineup_group 요소 직접 탐색 실패: {}", gameId, e.getMessage(), e);
                return null;
            }

            // 변경된 클래스 대응: 라인업 구역
            List<WebElement> teamSections = lineupWrapper.findElements(By.cssSelector("div[class^='Lineup_lineup_area__']"));
            if (teamSections.size() != 2) {
                log.warn("[{}] 팀 섹션이 2개가 아님 (크기: {})", gameId, teamSections.size());
                return null;
            }

            TeamLineup awayLineup = parseTeamLineup(driver2, teamSections.get(0), 1);   // away: 1~10
            TeamLineup homeLineup = parseTeamLineup(driver2, teamSections.get(1), 11);  // home: 11~20

            return RedisGameLineup.builder()
                    .away(awayLineup)
                    .home(homeLineup)
                    .build();

        } catch (Exception e) {
            log.error("[{}] 라인업 크롤링 중 예상치 못한 오류 발생: {}", gameId, e.getMessage(), e);
            return null;
        } finally {
            quitDriver(driver1);
            quitDriver(driver2);
        }
    }

    private TeamLineup parseTeamLineup(WebDriver kboDriver, WebElement teamElement, long playerIdStart) {
        List<WebElement> playerItems = teamElement.findElements(By.cssSelector(".Lineup_lineup_item__2AXR8"));
        List<PlayerInfo> players = new ArrayList<>();

        Long playerIdSeq = playerIdStart;

        for (WebElement item : playerItems) {
            try {
                String name = item.findElement(By.cssSelector(".Lineup_name__Q5oDC")).getText();
                String rawPosition = item.findElement(By.cssSelector(".Lineup_position__2fA4L")).getText();

                String position = "", handedness = "";
                String[] posSplit = rawPosition.split(",");
                if (posSplit.length >= 1) position = posSplit[0].trim();
                if (posSplit.length >= 2) handedness = posSplit[1].trim();

                String imageUrl = getImageFromKBO(kboDriver, name);

                players.add(PlayerInfo.builder()
                        .id(playerIdSeq++)
                        .name(name)
                        .position(position)
                        .handedness(handedness)
                        .imageUrl(imageUrl)
                        .build());

            } catch (Exception e) {
                log.warn("[라인업 파싱 실패] 선수 요소 파싱 중 오류 발생: {}", e.getMessage());
            }
        }

        PlayerInfo starterPitcher = players.get(0);
        List<PlayerInfo> starterBatters = new ArrayList<>(players.subList(1, players.size()));

        return new TeamLineup(starterPitcher, starterBatters);
    }

    private String getImageFromKBO(WebDriver driver, String playerName) {
        try {
            driver.get("https://www.koreabaseball.com/player/search.aspx");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("cphContents_cphContents_cphContents_txtSearchPlayerName")));
            input.clear();
            input.sendKeys(playerName);

            WebElement searchBtn = driver.findElement(By.id("cphContents_cphContents_cphContents_btnSearch"));
            searchBtn.click();

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("tbody > tr")));

            WebElement link = driver.findElement(By.cssSelector("tbody > tr > td:nth-child(2) a"));
            String href = link.getAttribute("href");
            String playerId = href.replaceAll("[^0-9]", "");

            if (!playerId.isEmpty()) {
                return "https://sports-phinf.pstatic.net/player/kbo/default/" + playerId + ".png?type=w150";
            }

        } catch (Exception e) {
            log.warn("[KBO 이미지 조회 실패] 선수명: {}, 이유: {}", playerName, e.getMessage());
        }

        return "선수 기본 이미지로 변경";
    }
}
