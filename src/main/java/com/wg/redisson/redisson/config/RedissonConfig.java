package com.wg.redisson.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonReactiveClient redissonReactiveClient(RedissonClient redissonClient) {
        return Redisson.createReactive(redissonClient.getConfig());
    }

    private static final String EXECUTOR_SERVICE_NAME = "rScheduledExecutor";

    @Bean(destroyMethod = "")
    public RScheduledExecutorService rScheduledExecutorService(
            final RedissonClient redissonClient,
            final BeanFactory beanFactory
    ) {
        final WorkerOptions workerOptions = WorkerOptions.defaults().workers(1).beanFactory(beanFactory);
        final ExecutorOptions executorOptions = ExecutorOptions.defaults()
                .taskRetryInterval(0, TimeUnit.SECONDS);
        final RScheduledExecutorService executorService = redissonClient
                .getExecutorService(EXECUTOR_SERVICE_NAME, executorOptions);
        executorService.registerWorkers(workerOptions);
        return executorService;
    }

}