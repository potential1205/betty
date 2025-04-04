package org.example.betty.domain.game.async;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LineupAsyncExecutor {

    @Async
    public void runAsync(Runnable task) {
        task.run();
    }
}
