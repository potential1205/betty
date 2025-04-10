package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.preview.*;
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

@Slf4j
@Service
public class GamePreviewScraper extends BaseScraper {

    @Value("${selenium.remote.urls}")
    private List<String> seleniumUrls;

    public TeamComparisonDto scrapePreview(String gameId, int seleniumIndex) {
        String url = "https://m.sports.naver.com/game/" + gameId + "/preview";
        WebDriver driver = createDriver(seleniumUrls.get(seleniumIndex));

        try {
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".TeamPower_team_info_area__1VWxM")));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".TeamPower_detail_area__UlnCk")));

            WebElement teamArea = driver.findElement(By.cssSelector(".TeamPower_team_info_area__1VWxM"));
            List<WebElement> teamLinks = teamArea.findElements(By.tagName("a"));
            if (teamLinks.size() < 2) {
                log.warn("[{}] 팀 링크 수 부족: {}", gameId, teamLinks.size());
                return null;
            }

            TeamSummaryInfo awaySummary = extractTeamSummary(teamLinks.get(0));
            TeamSummaryInfo homeSummary = extractTeamSummary(teamLinks.get(1));

            WebElement detailArea = driver.findElement(By.cssSelector(".TeamPower_detail_area__UlnCk"));
            List<WebElement> rows = detailArea.findElements(By.cssSelector("tbody tr"));
            if (rows.size() < 5) {
                log.warn("[{}] 프리뷰 테이블 row 수 부족: {}", gameId, rows.size());
                return null;
            }

            // 최근 경기 결과
            List<WebElement> tdList = rows.get(0).findElements(By.tagName("td"));
            if (tdList.size() < 2) {
                log.warn("[{}] 최근 경기 td 수 부족: {}", gameId, tdList.size());
                return null;
            }

            List<WebElement> recentAwayElements = tdList.get(0).findElements(By.tagName("span"));
            List<WebElement> recentHomeElements = tdList.get(1).findElements(By.tagName("span"));

            RecentGameResult awayRecent = new RecentGameResult(awaySummary.getTeamName(), extractResults(recentAwayElements));
            RecentGameResult homeRecent = new RecentGameResult(homeSummary.getTeamName(), extractResults(recentHomeElements));

            // 주요 지표 (승률, 타율, 평균자책)
            List<WebElement> winRateElements = rows.get(1).findElements(By.cssSelector(".TeamPower_score__2zqG4"));
            if (winRateElements.size() < 2) {
                log.warn("[{}] 승률 지표 비어있음: {}", gameId, winRateElements.size());
                return null;
            }

            List<WebElement> avgRow = rows.get(2).findElements(By.cssSelector(".TeamPower_score__2zqG4"));
            List<WebElement> eraRow = rows.get(3).findElements(By.cssSelector(".TeamPower_score__2zqG4"));
            if (avgRow.size() < 2 || eraRow.size() < 2) {
                log.warn("[{}] 타율 또는 평균자책 지표 부족", gameId);
                return null;
            }

            TeamStat awayStat = TeamStat.builder()
                    .teamName(awaySummary.getTeamName())
                    .winRate(parseDouble(winRateElements.get(0).getText()))
                    .avg(parseDouble(avgRow.get(0).getText()))
                    .era(parseDouble(eraRow.get(0).getText()))
                    .build();

            TeamStat homeStat = TeamStat.builder()
                    .teamName(homeSummary.getTeamName())
                    .winRate(parseDouble(winRateElements.get(1).getText()))
                    .avg(parseDouble(avgRow.get(1).getText()))
                    .era(parseDouble(eraRow.get(1).getText()))
                    .build();

            // 상대전적
            List<WebElement> recordTds = rows.get(4).findElements(By.tagName("td"));
            if (recordTds.size() < 2) {
                log.warn("[{}] 상대전적 컬럼 수 부족: {}", gameId, recordTds.size());
                return null;
            }

            HeadToHeadRecord awayRecord = parseVsRecord(awaySummary.getTeamName(), recordTds.get(0).findElements(By.tagName("span")));
            HeadToHeadRecord homeRecord = parseVsRecord(homeSummary.getTeamName(), recordTds.get(1).findElements(By.tagName("span")));

            return TeamComparisonDto.builder()
                    .awayTeam(awaySummary)
                    .homeTeam(homeSummary)
                    .awayRecent(awayRecent)
                    .homeRecent(homeRecent)
                    .awayStat(awayStat)
                    .homeStat(homeStat)
                    .awayRecord(awayRecord)
                    .homeRecord(homeRecord)
                    .build();

        } catch (Exception e) {
            log.error("[{}] 프리뷰 크롤링 실패: {}", gameId, e.getMessage(), e);
            return null;
        } finally {
            quitDriver(driver);
        }
    }

    private TeamSummaryInfo extractTeamSummary(WebElement teamLink) {
        String name = teamLink.findElement(By.cssSelector(".TeamPower_name_area__2WGfA")).getText();
        String rankText = teamLink.findElement(By.cssSelector(".TeamPower_rank__1hMAz")).getText();
        int rank = Integer.parseInt(rankText.replace("위", "").trim());

        List<WebElement> results = teamLink.findElements(By.cssSelector(".TeamPower_result__3y6nW"));
        int wins = Integer.parseInt(results.get(0).getText().replace("승", ""));
        int draws = Integer.parseInt(results.get(1).getText().replace("무", ""));
        int losses = Integer.parseInt(results.get(2).getText().replace("패", ""));

        return TeamSummaryInfo.builder()
                .teamName(name)
                .rank(rank)
                .wins(wins)
                .draws(draws)
                .losses(losses)
                .build();
    }

    private List<String> extractResults(List<WebElement> resultElements) {
        List<String> results = new ArrayList<>();
        for (WebElement el : resultElements) {
            results.add(el.getText().trim());
        }
        return results;
    }

    private HeadToHeadRecord parseVsRecord(String teamName, List<WebElement> elements) {
        try {
            int wins = Integer.parseInt(elements.get(0).getText().replace("승", "").replaceAll("[^0-9]", ""));
            int draws = Integer.parseInt(elements.get(1).getText().replace("무", "").replaceAll("[^0-9]", ""));
            int losses = Integer.parseInt(elements.get(2).getText().replace("패", "").replaceAll("[^0-9]", ""));
            return new HeadToHeadRecord(teamName, wins, draws, losses);
        } catch (Exception e) {
            log.warn("[{}] 상대전적 파싱 실패: {}", teamName, e.getMessage());
            return new HeadToHeadRecord(teamName, 0, 0, 0);
        }
    }

    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
