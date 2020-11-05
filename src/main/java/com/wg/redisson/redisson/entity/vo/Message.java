package com.wg.redisson.redisson.entity.vo;

import lombok.Data;

@Data
public class Message {
    private String targetId;
    private String messageText;
    private String userId;
}
