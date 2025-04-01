package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.entity.Teams;
import org.example.betty.domain.game.repository.TeamsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TeamsDataLoader implements CommandLineRunner {

    private final TeamsRepository teamsRepository;

    @Override
    public void run(String... args) {
        if (teamsRepository.count() > 0) {
            return;
        }

        teamsRepository.saveAll(List.of(
                new Teams("LT", "롯데 자이언츠", "LGT"),
                new Teams("WO", "키움 히어로즈", "KWN"),
                new Teams("NC", "NC 다이노스", "NCN"),
                new Teams("LG", "LG 트윈스", "LGT"),
                new Teams("OB", "두산 베어스", "OBB"),
                new Teams("KT", "KT 위즈", "KTT"),
                new Teams("SK", "SSG 랜더스", "SSG"),
                new Teams("HT", "KIA 타이거즈", "KIA"),
                new Teams("SS", "삼성 라이온즈", "SSL"),
                new Teams("HH", "한화 이글스", "HHE")
        ));
    }
}
