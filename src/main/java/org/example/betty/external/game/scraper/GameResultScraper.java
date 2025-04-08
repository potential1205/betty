package org.example.betty.external.game.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameResultScraper extends BaseScraper {

    @Value("${selenium.remote.urls}")
    private List<String> seleniumUrls;

    private final Map<String, WebDriver> driverMap = new ConcurrentHashMap<>();
    private final TeamRepository teamRepository;

    public void initDriver(String gameId, int index) {
        try {
            WebDriver driver = createDriver(seleniumUrls.get(index));
            driverMap.put(gameId, driver);
            log.info("[{}] GameResult WebDriver 초기화 완료", gameId);
        } catch (Exception e) {
            log.error("[{}] GameResult WebDriver 초기화 실패: {}", gameId, e.getMessage(), e);
        }
    }

    public PreVoteAnswer scrapeResult(String gameId) {
        WebDriver driver = driverMap.get(gameId);
        if (driver == null) {
            log.warn("[{}] WebDriver 없음 → 경기 결과 크롤링 중단", gameId);
            return null;
        }

        try {
            String year = gameId.substring(0, 4);
            String month = String.valueOf(Integer.parseInt(gameId.substring(4, 6)));
            String day = String.valueOf(Integer.parseInt(gameId.substring(6, 8)));
            String awayCode = gameId.substring(8, 10);
            String homeCode = gameId.substring(10, 12);

            String url = "https://statiz.sporki.com/schedule/?year=" + year + "&month=" + month;
            driver.get(url);

            Optional<String> awayTeamName = getTeamName(awayCode);
            Optional<String> homeTeamName = getTeamName(homeCode);
            if (awayTeamName.isEmpty() || homeTeamName.isEmpty()) {
                log.warn("[{}] team_code -> team_name 변환 실패", gameId);
                return null;
            }

            String awayTeamShort = awayTeamName.get().split(" ")[0];
            String homeTeamShort = homeTeamName.get().split(" ")[0];

            List<WebElement> dayCells = driver.findElements(By.cssSelector("td"));
            for (WebElement td : dayCells) {
                try {
                    WebElement dayElement = td.findElement(By.cssSelector("span.day"));
                    if (!dayElement.getText().trim().equals(day)) continue;

                    WebElement gamesDiv = td.findElement(By.cssSelector(".games"));
                    List<WebElement> gameLinks = gamesDiv.findElements(By.cssSelector("li a"));

                    for (WebElement game : gameLinks) {
                        List<WebElement> teams = game.findElements(By.cssSelector(".team"));
                        List<WebElement> scores = game.findElements(By.cssSelector(".score, .score.lead"));

                        if (teams.size() != 2 || scores.size() != 2) continue;

                        String team1 = teams.get(0).getText().trim();
                        String team2 = teams.get(1).getText().trim();

                        boolean isMatching =
                                (team1.equals(homeTeamShort) && team2.equals(awayTeamShort)) ||
                                        (team2.equals(homeTeamShort) && team1.equals(awayTeamShort));

                        if (!isMatching) continue;

                        log.info("[{}] 해당 경기 링크 발견 → {} vs {}", gameId, team1, team2);

                        // 승리팀 결정
                        List<WebElement> scoreElements = game.findElements(By.cssSelector(".score"));
                        List<WebElement> scoreLeadElements = game.findElements(By.cssSelector(".score.lead"));

                        String winner = "";
                        String loser = "";

                        if (scoreLeadElements.size() > 0) {
                            WebElement leadScore = scoreLeadElements.get(0);
                            List<WebElement> spans = game.findElements(By.cssSelector("span"));

                            boolean winnerFound = false;
                            for (int i = 0; i < spans.size(); i++) {
                                if (spans.get(i).equals(leadScore)) {
                                    for (int j = i - 1; j <= i + 1; j++) {
                                        if (j >= 0 && j < spans.size()) {
                                            WebElement maybeTeam = spans.get(j);
                                            if (maybeTeam.getAttribute("class").contains("team")) {
                                                winner = maybeTeam.getText().trim();
                                                if (winner.equals(team1)) loser = team2;
                                                else loser = team1;
                                                winnerFound = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (winnerFound) break;
                            }
                        }

                        log.info("[{}] 결과: 승리팀 = {}, 패배팀 = {}", gameId, winner, loser);

                        String summaryUrl = game.getAttribute("href");
                        driver.get(summaryUrl);

                        List<WebElement> rows = driver.findElements(By.cssSelector(".table_type03 tbody tr"));
                        String mvpName = null;
                        String mvpHref = null;
                        String mvpTeam = null;

                        for (WebElement row : rows) {
                            List<WebElement> tds = row.findElements(By.tagName("td"));
                            if (tds.size() >= 2 && tds.get(0).getText().trim().equals("1")) {
                                WebElement mvpLinkElement = tds.get(1).findElement(By.tagName("a"));
                                mvpName = mvpLinkElement.getText().trim();
                                mvpHref = mvpLinkElement.getAttribute("href");

                                log.info("[{}] MVP 이름 파싱 결과: {}", gameId, mvpName);
                                log.info("[{}] mvpHref: {}", gameId, mvpHref);

                                driver.get(mvpHref);

                                WebElement conDiv = driver.findElement(By.cssSelector("div.con"));
                                List<WebElement> spans = conDiv.findElements(By.tagName("span"));
                                if (!spans.isEmpty()) {
                                    mvpTeam = spans.get(0).getText().trim();
                                    log.info("[{}] MVP 팀명 파싱 결과: {}", gameId, mvpTeam);
                                }
                                break;
                            }
                        }

                        return PreVoteAnswer.builder()
                                .teamA(homeCode)
                                .teamB(awayCode)
                                .winnerTeam(winner)
                                .loserTeam(loser)
                                .mvpName(mvpName)
                                .mvpTeam(mvpTeam)
                                .build();
                    }

                } catch (Exception ignored) {}
            }

            return null;

        } catch (Exception e) {
            log.error("[{}] 경기 결과 크롤링 실패: {}", gameId, e.getMessage(), e);
            return null;
        } finally {
            quitDriver(driver);
            driverMap.remove(gameId);
        }
    }

    private Optional<String> getTeamName(String code) {
        return teamRepository.findByTeamCode(code).map(Team::getTeamName);
    }
}