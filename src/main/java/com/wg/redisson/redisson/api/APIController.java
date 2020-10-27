package com.wg.redisson.redisson.api;

import com.wg.redisson.redisson.entity.User;
import com.wg.redisson.redisson.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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


}
