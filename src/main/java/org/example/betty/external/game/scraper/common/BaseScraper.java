package org.example.betty.external.game.scraper.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;

public abstract class BaseScraper {

    @Value("${selenium.remote.url}")
    private String url;

    /**
     * WebDriver 공통 셋업
     */
    protected WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");

        try{
            return new RemoteWebDriver(new URL(url), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
