package com.wg.redisson.redisson.service;

import com.wg.redisson.redisson.dao.LoginEventRepository;
import com.wg.redisson.redisson.entity.po.LoginEvent;
import io.r2dbc.postgresql.api.Notification;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.api.PostgresqlResult;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class LoginEventService {
    final LoginEventRepository repository;
    final DatabaseClient databaseClient;

    final ConnectionFactory pgConnectionFactory;
    PostgresqlConnection sender;

    @PostConstruct
    private void postConstruct() {
        sender = Mono.from(pgConnectionFactory.create())
                .cast(PostgresqlConnection.class)
                .block();

        sender.createStatement("LISTEN login_event_notification")
                .execute()
                .flatMap(PostgresqlResult::getRowsUpdated).subscribe();
    }

    @PreDestroy
    private void preDestroy() {
        sender.close().subscribe();
    }

    public Flux<CharSequence> getStream() {
        return sender.getNotifications().map(Notification::getParameter);
    }

    public Mono<Void> save(String username) {
        return repository.save(new LoginEvent(username, LocalDateTime.now())).then();
    }
}

