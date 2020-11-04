package com.wg.redisson.redisson.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table
public class LoginEvent {

    @Id
    Integer id;

    String username;

    LocalDateTime loginTime;

    public LoginEvent(String username, LocalDateTime loginTime) {
        this.username = username;
        this.loginTime = loginTime;
    }

}