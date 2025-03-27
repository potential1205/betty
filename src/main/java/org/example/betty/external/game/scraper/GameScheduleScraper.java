package org.example.betty.external.game.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.dto.GameSchedule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameScheduleScraper {

    /**
     * 월별 경기 일정 크롤러
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
     * 월별 상세 경기 정보 스크래핑
     */
    public List<GameSchedule> scrapeMonthlySchedule(String url) {
        String driverPath = System.getProperty("user.dir") + "/src/main/resources/chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        List<GameSchedule> gameList = new ArrayList<>();

        try {
            driver.get(url);
            Thread.sleep(2000);

            // 시즌 년도 추출
            String season = driver.findElement(By.className("CalendarDate_number__SLzsc")).getText();

            List<WebElement> matchGroups = driver.findElements(By.className("ScheduleLeagueType_match_list_group__18ML9"));

            for (WebElement group : matchGroups) {
                // 경기일자 추출
                WebElement dateEl = group.findElement(By.className("ScheduleLeagueType_title__2Kalm"));
                String dateText = dateEl.getText();
                String gameDate = convertToFullDate(season,dateText);

                // 일자별 경기정보 추출
                List<WebElement> games = group.findElements(By.className("MatchBox_item_content__3SGZf"));
                for (WebElement game : games) {
                    String startTime = game.findElement(By.className("MatchBox_time__nIEfd")).getText().split("\n")[1].trim();;
                    String stadium = game.findElement(By.className("MatchBox_stadium__13gft")).getText().split("\n")[1].trim();;
                    List<WebElement> teams = game.findElements(By.className("MatchBoxHeadToHeadArea_name_info__ElWd2"));
                    String awayTeam = teams.get(0).getText();
                    String homeTeam = teams.get(1).getText().replace("홈", "").replaceAll("\\s+", "").trim();;
                    String status = game.findElement(By.className("MatchBox_status__2pbzi")).getText();

                    GameSchedule schedule = new GameSchedule(season, gameDate, startTime, stadium, awayTeam, homeTeam, status);
                    log.info(schedule.toString());
                    gameList.add(schedule);
                }
            }
            log.info("월별 경기 크롤링 완료: {}", url);

        } catch (Exception e) {
            log.error("경기 일정 크롤링 실패: {}", e.getMessage());
        } finally {
            driver.quit();
        }

        return gameList;
    }


    /**
     * 경기일자 조합 메서드
     * @param season
     * @param dateText
     * @return
     */
    private String convertToFullDate(String season, String dateText) {
        try {
            String[] parts = dateText.split(" ");
            String monthDay = parts[0];
            String day = parts[1];

            int month = Integer.parseInt(monthDay.replace("월", ""));
            int dayNum = Integer.parseInt(day.replace("일", ""));
            return String.format("%s-%02d-%02d", season, month, dayNum);
        } catch (Exception e) {
            log.error("날짜 변환 실패: {}", dateText);
            return season + "-01-01"; // fallback
        }
    }




}
