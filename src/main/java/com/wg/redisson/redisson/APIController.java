package com.wg.redisson.redisson;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
public class APIController {


    @Autowired
    RedissonReactiveClient redissonClient;

    @Autowired
    RScheduledExecutorService r;

    @GetMapping("/user")
    public Mono<String> get(){
        RAtomicLongReactive hello = redissonClient.getAtomicLong("hello");
        return hello.get().map(x-> "hello: "+x);
    }

    @PostConstruct
    public void postConstruct() {
        r.schedule(new ScheduledRunnableTask("hello"), CronSchedule.of("0/3 * * * * ?”"));
    }
}
