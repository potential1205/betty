package org.example.betty.external.game.scraper.parser;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PlayerRelayInfoParser {

    public static PlayerRelayInfo extractPitcherInfo(WebElement pitcherBox, WebDriver driver, String gameId) {
        try {
            String name = textOrNull(pitcherBox, By.cssSelector(".RelayList_name__1THfS"));
            String position = textOrNull(pitcherBox, By.cssSelector(".RelayList_position__M6m4z"));
            String inningPitched = null;
            String totalPitches = null;

            List<WebElement> dtElements = pitcherBox.findElements(By.cssSelector(".RelayList_sub_info__KV9-f dt"));
            List<WebElement> ddElements = pitcherBox.findElements(By.cssSelector(".RelayList_sub_info__KV9-f dd"));

            for (int i = 0; i < dtElements.size(); i++) {
                String label = dtElements.get(i).getText().trim();
                String value = ddElements.get(i).getText().trim();
                if (label.contains("이닝")) {
                    inningPitched = value;
                } else if (label.contains("투구수")) {
                    totalPitches = value;
                }
            }

            return PlayerRelayInfo.builder()
                    .name(name)
                    .position(position)
                    .avg(null)
                    .inningPitched(inningPitched)
                    .totalPitches(totalPitches)
                    .summaryText(null)
                    .build();

        } catch (Exception e) {
            log.warn("[{}] Pitcher 정보 파싱 실패: {}", gameId, e.getMessage());
            return null;
        }
    }

    public static PlayerRelayInfo extractBatterInfo(WebElement batterBox, WebDriver driver, String gameId) {
        try {
            String name = textOrNull(batterBox, By.cssSelector(".RelayList_name__1THfS"));
            String position = textOrNull(batterBox, By.cssSelector(".RelayList_position__M6m4z"));
            String avg = textOrNull(batterBox, By.cssSelector(".RelayList_average__GnAvE"));

            return PlayerRelayInfo.builder()
                    .name(name)
                    .position(position)
                    .avg(avg)
                    .inningPitched(null)
                    .totalPitches(null)
                    .summaryText(null)
                    .build();

        } catch (Exception e) {
            log.warn("[{}] Batter 정보 파싱 실패: {}", gameId, e.getMessage());
            return null;
        }
    }


    public static PlayerRelayInfo extractPreviousBatterInfo(WebElement batterBox, WebDriver driver, String gameId) {
        try {
            String name = textOrNull(batterBox, By.cssSelector(".RelayList_name__1THfS"));
            String position = textOrNull(batterBox, By.cssSelector(".RelayList_position__M6m4z"));
            String avg = textOrNull(batterBox, By.cssSelector(".RelayList_average__GnAvE"));

            // ⚾️ summaryText 파싱
            String summaryText = null;
            try {
                WebElement summaryBox = batterBox.findElement(By.cssSelector(".RelayList_main_info__zGpeF"));
                List<WebElement> lines = summaryBox.findElements(By.cssSelector(".RelayList_text__tFNjV"));
                List<String> parts = new ArrayList<>();
                for (WebElement el : lines) {
                    String text = el.getText().trim();
                    if (!text.isEmpty()) parts.add(text);
                }
                summaryText = String.join(" / ", parts);
            } catch (Exception e) {
                log.trace("[{}] 직전 타자 summaryText 없음 (아직 없음 or 이닝 시작 등)", gameId);
            }

            // ✅ pitchResults 파싱
            List<String> pitchResults = new ArrayList<>();
            try {
                List<WebElement> resultElements = batterBox.findElements(
                        By.cssSelector(".RelayList_history_list__13jzg .RelayList_text__tFNjV")
                );
                for (WebElement el : resultElements) {
                    String text = el.getText().trim();
                    if (!text.isEmpty()) pitchResults.add(text);
                }
            } catch (Exception e) {
                log.warn("[{}] 직전 타자 pitchResults 파싱 실패: {}", gameId, e.getMessage());
            }

            return PlayerRelayInfo.builder()
                    .name(name)
                    .position(position)
                    .avg(avg)
                    .inningPitched(null)
                    .totalPitches(null)
                    .summaryText(summaryText)
                    .pitchResults(pitchResults)
                    .build();

        } catch (Exception e) {
            log.warn("[{}] 직전 타자 정보 파싱 실패: {}", gameId, e.getMessage());
            return null;
        }
    }


    private static String textOrNull(WebElement root, By selector) {
        try {
            return root.findElement(selector).getText().trim();
        } catch (Exception e) {
            return null;
        }
    }
}
