package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LineupAsyncExecutor {

    /**
     * Runnable task를 비동기(@Async)로 실행
     */
    @Async
    public void runAsync(Runnable task) {
        task.run();
    }
}
