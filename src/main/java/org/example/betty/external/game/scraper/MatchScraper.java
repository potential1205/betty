package org.example.betty.external.game.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchScraper {

    public String scrapeMatchData(String gameUrl) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(gameUrl);
            Thread.sleep(3000);

            List<WebElement> elements = driver.findElements(By.id("wrap"));
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < elements.size(); i++) {
                result.append(i + 1).append("번째 단락: ").append(elements.get(i).getText()).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "오류 발생: " + e.getMessage();
        } finally {
            driver.quit();
        }
    }
}
