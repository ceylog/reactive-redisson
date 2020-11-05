package com.wg.redisson.redisson.config;

import com.wg.redisson.redisson.handler.ChatHandler;
import com.wg.redisson.redisson.handler.EchoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class WebsocketConfig {

    @Bean
    public HandlerMapping websocketMapping() {
        SimpleUrlHandlerMapping urlHandlerMapping = new SimpleUrlHandlerMapping();
        Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();
        handlerMap.put("/ws/echo",new EchoHandler());
        handlerMap.put("/ws/chat",new ChatHandler());
        urlHandlerMapping.setUrlMap(handlerMap);
        urlHandlerMapping.setOrder(-1);
        return urlHandlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter(){
        return new WebSocketHandlerAdapter();
    }
}
