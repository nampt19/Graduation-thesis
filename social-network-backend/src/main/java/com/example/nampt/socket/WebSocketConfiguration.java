package com.example.nampt.socket;


import com.example.nampt.service.IChatService;
import com.example.nampt.service.ServiceImpl.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandlerChatGroup(), "/websocket/group").setAllowedOrigins("*");
        registry.addHandler(new WebSocketHandlerCreateGroup(), "/websocket/createGroup").setAllowedOrigins("*");
        registry.addHandler(new WebSocketHandlerChatSingle(), "/websocket/single").setAllowedOrigins("*");
    }


}
