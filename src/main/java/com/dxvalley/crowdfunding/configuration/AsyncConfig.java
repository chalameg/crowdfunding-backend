package com.dxvalley.crowdfunding.configuration;

import com.dxvalley.crowdfunding.exception.AsyncMethodException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {
    private final ThreadPoolProperties threadPoolProperties;
    private final CustomAsyncExceptionHandler customAsyncExceptionHandler;

    @Override
    public Executor getAsyncExecutor() {
        return AsyncConfigurer.super.getAsyncExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return customAsyncExceptionHandler;
    }

    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setThreadNamePrefix("Deboo-Admin-Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }

    @Component
    @ConfigurationProperties(prefix = "threadpool")
    @Setter
    private class ThreadPoolProperties {
        @Min(value = 1, message = "Core pool size must be at least 1")
        private int corePoolSize;

        @Min(value = 1, message = "Max pool size must be at least 1")
        private int maxPoolSize;

        @Min(value = 1, message = "Queue capacity must be at least 1")
        private int queueCapacity;


        public int getCorePoolSize() {
            return corePoolSize <= 0 ? 10 : corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize <= 0 ? 20 : maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity <= 0 ? 50 : queueCapacity;
        }
    }

    @Component
    private class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
            throw new AsyncMethodException(throwable.getMessage());
        }

    }
}