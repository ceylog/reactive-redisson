package com.wg.redisson.redisson.api;

import com.wg.redisson.redisson.entity.User;
import com.wg.redisson.redisson.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class APIController {

    private final RedissonReactiveClient redissonClient;

    private final UserService userService;

    @GetMapping("/test")
    public Mono<String> get() {
        RAtomicLongReactive hello = redissonClient.getAtomicLong("hello");
        return hello.get().map(x -> "hello: " + x);
    }

    @GetMapping("/all")
    public Flux<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/byid/{id}")
    public Mono<User> getById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping(value = "/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sse(){
        return Flux.interval(Duration.ofMillis(1000)).map(val -> ServerSentEvent.<String>builder()
                .id(UUID.randomUUID().toString())
                .event("test_event")
                .data(val.toString())
                .build());
    }

}
