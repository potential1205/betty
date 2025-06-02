package org.example.betty.external.game.scraper.common;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.util.List;

@Slf4j
public abstract class BaseScraper {

    @Value("${selenium.remote.urls}")
    protected List<String> urls;

    protected WebDriver createDriver() {
        return createDriver(0); // 기본적으로 index 0 사용
    }

    protected WebDriver createDriver(int index) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");

        try{
            String url = urls.get(index);
            return new RemoteWebDriver(new URL(url), options);
        } catch (Exception e) {
            log.error("[ERROR] WebDriver 생성 실패 - {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    protected WebDriver createDriver(String seleniumUrl) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");

        try{
            return new RemoteWebDriver(new URL(seleniumUrl), options);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ERROR] WebDriver 생성 실패 - {}", e.getMessage());
            return null;
        }
    }


    /**
     * WebDriver 안전 종료
     */
    protected void quitDriver(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 공통 예외 로깅
     */
    protected void handleException(Exception e, String context) {
        System.err.println("[ERROR] " + context + " - " + e.getMessage());
    }
}
