package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import org.example.betty.external.game.scraper.LineupScraper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
@RequiredArgsConstructor
public class LineupScraperTestRunner implements CommandLineRunner {

    private final LineupScraper lineupScraper;

    @Override
    public void run(String... args) {
        String testGameId = "20250330HTHH02025";
        lineupScraper.scrapeLineup(testGameId); // 앱 시작 시 자동 호출
    }
}
