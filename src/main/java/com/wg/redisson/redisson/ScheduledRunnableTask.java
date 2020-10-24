package com.wg.redisson.redisson;

import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ScheduledRunnableTask implements Runnable, Serializable {
    private static Logger log = LoggerFactory.getLogger(ScheduledRunnableTask.class);
    @RInject
    private RedissonClient redissonClient;
    private String objectName;

    public ScheduledRunnableTask(String objectName){
        super();
        this.objectName = objectName;
    }


    public void run() {
        Long l = redissonClient.getAtomicLong(objectName).incrementAndGet();
        log.info("-{}",l);
    }
}