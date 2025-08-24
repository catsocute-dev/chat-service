package com.catsocute.chat_service.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@org.springframework.context.annotation.Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class NettySocketIOConfig {
    SocketHandler socketHandler;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration configuration = new Configuration();
        configuration.setPort(8099); //set port
        configuration.setOrigin("*"); // CORS config

        // create SocketIO server
        SocketIOServer socketIOServer = new SocketIOServer(configuration);

        //handler register
        socketIOServer.addListeners(socketHandler);

        return socketIOServer;
    }

    //start socket server
    @EventListener(ApplicationReadyEvent.class)
    public void startSocketServer() {
        socketIOServer().start();
        log.info("Socket server started");
    };

    //shutdown socket server
    @PreDestroy
    public void stopSocketServer() {
        socketIOServer().stop();
        log.info("Socket server stopped");
    }
}
