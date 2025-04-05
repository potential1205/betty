package org.example.betty.external.game.scraper.parser;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
public class GameRelayInfoParser {

    private static final Duration WAIT_DURATION = Duration.ofSeconds(10);

    public static String extractInningInfo(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".OffenseTitle_title__1LKfI")));

            return element.getText().trim();
        } catch (NoSuchElementException e) {
            log.warn("[이닝 정보 없음] 요소가 존재하지 않음: {}", e.getMessage());
            return null;
        }
    }




    public static List<String> extractPitchResult(WebDriver driver) {
        List<String> result = new ArrayList<>();
        try {
            // 현재 타자 블록을 먼저 찾고
            WebElement currentBatterBox = driver.findElement(
                    By.cssSelector(".RelayList_player_area__2ur0q.RelayList_type_current__eUw25")
            );

            // 해당 블록 안의 투구 결과 텍스트만 가져오기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            List<WebElement> textElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".RelayList_player_area__2ur0q.RelayList_type_current__eUw25 .RelayList_history_list__13jzg .RelayList_text__tFNjV"))
            );

            for (WebElement el : textElements) {
                String text = el.getText().trim();
                if (!text.isEmpty()) {
                    result.add(text);
                }
            }
        } catch (Exception e) {
            return result;
        }

        return result;
    }







    public static List<String> extractNextBatterNames(WebDriver driver) {
        List<String> result = new ArrayList<>();
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(
                    ".RelayList_waiting_player_area__3ta0- li"
            ));

            for (WebElement el : elements) {
                result.add(el.getText().trim());
            }
        } catch (Exception e) {
            log.warn("[대기 타자 목록 파싱 실패] {}", e.getMessage());
        }
        return result;
    }


    public static List<String> extractRunnerOnBase(WebDriver driver) {
        List<String> result = new ArrayList<>();
        try {
            Map<String, String> baseClassMap = Map.of(
                    "1루", ".GroundView_pre_b1__1zTEh",
                    "2루", ".GroundView_pre_b2__3AjFN",
                    "3루", ".GroundView_pre_b3__1qFiI"
            );

            for (Map.Entry<String, String> entry : baseClassMap.entrySet()) {
                List<WebElement> elements = driver.findElements(By.cssSelector(entry.getValue()));
                if (!elements.isEmpty()) {
                    String playerName = elements.get(0).getText().replace("진출", "").trim(); // "천성호"
                    result.add(entry.getKey() + " " + playerName); // "1루 천성호"
                }
            }

        } catch (Exception e) {
            log.warn("[주자 상황 파싱 실패] {}", e.getMessage());
        }
        return result;
    }

    public static String extractScore(WebDriver driver) {
        try {
            WebElement homeTeamEl = driver.findElement(By.cssSelector(".MatchBox_home__MPL6D .MatchBox_name__11AyG"));
            WebElement awayTeamEl = driver.findElement(By.cssSelector(".MatchBox_away__1rDsC .MatchBox_name__11AyG"));

            WebElement homeScoreEl = driver.findElement(By.cssSelector(".MatchBox_home__MPL6D .MatchBox_score__33SVc"));
            WebElement awayScoreEl = driver.findElement(By.cssSelector(".MatchBox_away__1rDsC .MatchBox_score__33SVc"));

            // ✅ 팀 이름에서 "홈", "원정" 등 badge 제거
            String homeTeam = homeTeamEl.getText().replaceAll("홈", "").replaceAll("원정", "").trim();
            String awayTeam = awayTeamEl.getText().replaceAll("홈", "").replaceAll("원정", "").trim();

            // ✅ 점수에서 "점수" 제거
            String homeScore = homeScoreEl.getText().replace("점수", "").trim();
            String awayScore = awayScoreEl.getText().replace("점수", "").trim();

            return homeTeam + " " + homeScore + " : " + awayTeam + " " + awayScore;

        } catch (Exception e) {
            log.warn("[점수 추출 실패] {}", e.getMessage());
            return null;
        }
    }



    public static int extractOutCount(WebDriver driver) {
        try {
            WebElement outArea = driver.findElement(By.cssSelector(".PTS_status_o__FRsiI"));
            List<WebElement> spans = outArea.findElements(By.tagName("span"));

            int count = 0;
            for (WebElement span : spans) {
                String cls = span.getAttribute("class");
                if (cls != null && !cls.trim().isEmpty()) {
                    count++;
                }
            }

            return count;
        } catch (Exception e) {
            log.warn("[아웃 카운트 파싱 실패] {}", e.getMessage());
            return 0;
        }
    }





}
