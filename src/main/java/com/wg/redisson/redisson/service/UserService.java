package com.wg.redisson.redisson.service;

import com.wg.redisson.redisson.dao.UserRepository;
import com.wg.redisson.redisson.entity.User;
import com.wg.redisson.redisson.task.ScheduledRunnableTask;
import lombok.RequiredArgsConstructor;
import org.redisson.api.CronSchedule;
import org.redisson.api.RScheduledExecutorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RScheduledExecutorService rScheduledExecutorService;

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> save(User u) {
        return userRepository.save(u);
    }

    public void update(User u) {
        userRepository.save(u);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id).subscribe();
    }

    @PostConstruct
    public void postConstruct() {
        rScheduledExecutorService.schedule(new ScheduledRunnableTask("hello"), CronSchedule.of("0/3 * * * * ?”"));
    }
}
