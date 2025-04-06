package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TeamsDataLoader implements CommandLineRunner {

    private final TeamRepository teamsRepository;

    @Override
    public void run(String... args) {
        if (teamsRepository.count() > 0) {
            return;
        }

        teamsRepository.saveAll(List.of(
                new Team("LT", "롯데 자이언츠", "LGT"),
                new Team("WO", "키움 히어로즈", "KWN"),
                new Team("NC", "NC 다이노스", "NCN"),
                new Team("LG", "LG 트윈스", "LGT"),
                new Team("OB", "두산 베어스", "OBB"),
                new Team("KT", "KT 위즈", "KTT"),
                new Team("SK", "SSG 랜더스", "SSG"),
                new Team("HT", "KIA 타이거즈", "KIA"),
                new Team("SS", "삼성 라이온즈", "SSL"),
                new Team("HH", "한화 이글스", "HHE")
        ));
    }
}
