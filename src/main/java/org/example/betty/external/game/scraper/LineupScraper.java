package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerInfo;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.TeamLineup;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LineupScraper extends BaseScraper {

    public RedisGameLineup scrapeLineup(String gameId) {

        WebDriver driver1 = createDriver(); // 라인업 페이지용
        WebDriver driver2 = createDriver(); // KBO 선수검색용

        try {
            String url = "https://m.sports.naver.com/game/" + gameId + "/lineup";
            driver1.get(url);

            new WebDriverWait(driver1, Duration.ofSeconds(10))
                    .until(d -> d.findElements(By.cssSelector(".lineup_group")).size() > 0);

            WebElement lineupWrapper = driver1.findElement(By.cssSelector(".lineup_group"));
            List<WebElement> teamSections = lineupWrapper.findElements(By.cssSelector(".Lineup_lineup_area__2aNOv"));

            TeamLineup awayLineup = parseTeamLineup(driver2, teamSections.get(0));
            TeamLineup homeLineup = parseTeamLineup(driver2, teamSections.get(1));

            return RedisGameLineup.builder()
                    .away(awayLineup)
                    .home(homeLineup)
                    .build();

        } catch (Exception e) {
            handleException(e, "LineupScraper - " + gameId);
            return null;
        } finally {
            quitDriver(driver1);
            quitDriver(driver2);
        }
    }

    private TeamLineup parseTeamLineup(WebDriver kboDriver, WebElement teamElement) {
        List<WebElement> playerItems = teamElement.findElements(By.cssSelector(".Lineup_lineup_item__32s4M"));
        List<PlayerInfo> players = new ArrayList<>();

        for (WebElement item : playerItems) {
            try {
                String name = item.findElement(By.cssSelector(".Lineup_name__jV19m")).getText();
                String rawPosition = item.findElement(By.cssSelector(".Lineup_position__265hb")).getText();

                String position = "", handedness = "";
                String[] posSplit = rawPosition.split(",");
                if (posSplit.length >= 1) position = posSplit[0].trim();
                if (posSplit.length >= 2) handedness = posSplit[1].trim();

                String imageUrl = null;
                List<WebElement> imgs = item.findElements(By.cssSelector("img[src^='https://sports-phinf.pstatic.net']"));
                if (!imgs.isEmpty()) {
                    imageUrl = imgs.get(0).getAttribute("src");
                }
                if (imageUrl == null || imageUrl.isBlank()) {
                    imageUrl = getImageFromKBO(kboDriver, name);
                }

                players.add(PlayerInfo.builder()
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
        List<PlayerInfo> starterBatters = players.subList(1, players.size());

        return new TeamLineup(starterPitcher, starterBatters);
    }

    private String getImageFromKBO(WebDriver driver, String playerName) {
        try {
            driver.get("https://www.koreabaseball.com/player/search.aspx");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement input = wait.until(d -> d.findElement(By.id("cphContents_cphContents_cphContents_txtSearchPlayerName")));
            input.clear();
            input.sendKeys(playerName);

            WebElement searchBtn = driver.findElement(By.id("cphContents_cphContents_cphContents_btnSearch"));
            searchBtn.click();

            wait.until(d -> d.findElements(By.cssSelector("tbody > tr")).size() > 0);

            WebElement link = driver.findElement(By.cssSelector("tbody > tr > td:nth-child(2) a"));
            String href = link.getAttribute("href"); // 예: javascript:goDetail('55633')
            String playerId = href.replaceAll("[^0-9]", "");

            if (!playerId.isEmpty()) {
                return "https://sports-phinf.pstatic.net/player/kbo/default/" + playerId + ".png?type=w150";
            }

        } catch (Exception e) {
            log.warn("[KBO 사이트에서 이미지 조회 실패] 선수명: {}, 이유: {}", playerName, e.getMessage());
        }

        return "선수 기본 이미지로 변경";
    }
}
