package org.example.betty.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ExecutorConfig implements AsyncConfigurer {

    // @Async용
    @Override
    @Bean("asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(5);
        exec.setMaxPoolSize(10);
        exec.setQueueCapacity(50);
        exec.setThreadNamePrefix("AsyncExecutor-");
        exec.initialize();
        return exec;
    }

    // schedule용
    @Bean("taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("GameScheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
