package org.example.betty.external.game.scraper;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.common.BaseScraper;
import org.example.betty.external.game.scraper.dto.GameSchedule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GameScheduleScraper extends BaseScraper {

    /**
     * 경기 일정 전체 저장
     */
    public List<GameSchedule> scrapeAllMonthsSchedule() {
        List<GameSchedule> allGames = new ArrayList<>();

        for (int month = 3; month <= 8; month++) {
            String date = String.format("2025-%02d-01", month);
            String url = "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=" + date;
            List<GameSchedule> games = scrapeMonthlySchedule(url);
            allGames.addAll(games);
        }

        return allGames;
    }

    /**
     * 월별 경기 일정 크롤링
     */
    public List<GameSchedule> scrapeMonthlySchedule(String url) {
        WebDriver driver = createDriver();
        List<GameSchedule> gameList = new ArrayList<>();

        try {
            driver.get(url);
            Thread.sleep(2000); // 임시 대기 (WebDriverWait으로 대체 가능)

            String season = driver.findElement(By.className("CalendarDate_number__SLzsc")).getText();
            List<WebElement> matchGroups = driver.findElements(By.className("ScheduleLeagueType_match_list_group__18ML9"));

            for (WebElement group : matchGroups) {
                WebElement dateEl = group.findElement(By.className("ScheduleLeagueType_title__2Kalm"));
                String gameDate = convertToFullDate(season, dateEl.getText());

                List<WebElement> games = group.findElements(By.className("MatchBox_item_content__3SGZf"));
                for (WebElement game : games) {
                    String startTime = game.findElement(By.className("MatchBox_time__nIEfd")).getText().split("\n")[1].trim();
                    String stadium = game.findElement(By.className("MatchBox_stadium__13gft")).getText().split("\n")[1].trim();
                    List<WebElement> teams = game.findElements(By.className("MatchBoxHeadToHeadArea_name_info__ElWd2"));
                    String awayTeam = teams.get(0).getText();
                    String homeTeam = teams.get(1).getText().replace("홈", "").replaceAll("\\s+", "").trim();
                    String status = game.findElement(By.className("MatchBox_status__2pbzi")).getText();

                    GameSchedule schedule = new GameSchedule(season, gameDate, startTime, stadium, awayTeam, homeTeam, status);
                    log.info(schedule.toString());
                    gameList.add(schedule);
                }
            }

            log.info("월별 경기 크롤링 완료: {}", url);

        } catch (Exception e) {
            handleException(e, "GameScheduleScraper");
        } finally {
            quitDriver(driver);
        }

        return gameList;
    }

    /**
     * 날짜 텍스트 → yyyy-MM-dd 형식으로 변환
     */
    private String convertToFullDate(String season, String dateText) {
        try {
            String[] parts = dateText.split(" ");
            int month = Integer.parseInt(parts[0].replace("월", ""));
            int day = Integer.parseInt(parts[1].replace("일", ""));
            return String.format("%s-%02d-%02d", season, month, day);
        } catch (Exception e) {
            log.error("날짜 변환 실패: {}", dateText);
            return season + "-01-01";
        }
    }
}
