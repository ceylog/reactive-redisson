package com.wg.redisson.redisson.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wg.redisson.redisson.entity.vo.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler implements WebSocketHandler {

    private static final ObjectMapper                  objectMapper = new ObjectMapper();
    public static        Map<String, WebSocketSession> userMap      = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String query = webSocketSession.getHandshakeInfo().getUri().getQuery();
        Map<String, String> queryMap = getQueryMap(query);
        String userId = queryMap.getOrDefault("userId", "");
        userMap.put(userId, webSocketSession);
        return webSocketSession.receive().flatMap(webSocketMessage -> {
            String payload = webSocketMessage.getPayloadAsText();
            Message message;
            try {
                message = objectMapper.readValue(payload, Message.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return webSocketSession.send(Mono.just(webSocketSession.textMessage(e.getMessage())));
            }
            String targetId = message.getTargetId();
            if (userMap.containsKey(targetId)) {
                WebSocketSession targetSession = userMap.get(targetId);
                if (null != targetSession) {
                    WebSocketMessage textMessage = targetSession.textMessage(payload);
                    return targetSession.send(Mono.just(textMessage));
                }
            }
            return webSocketSession.send(Mono.just(webSocketSession.textMessage("用户不在线")));
        }).then().doFinally(signal -> userMap.remove(userId));
    }


    private Map<String, String> getQueryMap(String queryStr) {
        Map<String, String> queryMap = new HashMap<>();
        if (!StringUtils.isEmpty(queryStr)) {
            String[] queryParam = queryStr.split("&");
            Arrays.stream(queryParam).forEach(s -> {
                String[] kv = s.split("=", 2);
                String value = kv.length == 2 ? kv[1] : "";
                queryMap.put(kv[0], value);
            });
        }
        return queryMap;
    }
}
