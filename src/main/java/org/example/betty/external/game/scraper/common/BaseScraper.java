package org.example.betty.external.game.scraper.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public abstract class BaseScraper {

    /**
     * WebDriver 공통 셋업
     */
    protected WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");

        return new ChromeDriver(options);
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
