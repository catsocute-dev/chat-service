package com.catsocute.chat_service.config;

import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.catsocute.chat_service.constant.SecurityConstants;
import com.catsocute.chat_service.constant.UserConstants;
import com.catsocute.chat_service.dto.request.IntrospectRequest;
import com.catsocute.chat_service.service.AuthenticationService;
import com.catsocute.chat_service.service.SessionService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocketHandler {
    AuthenticationService authenticationService;
    SessionService sessionService;

    // on connect
    @OnConnect
    public void onConnect(SocketIOClient client) throws ParseException, JOSEException {
        // CHECK AUTHENTICATION
        String userId = extractUserIdFromToken(client);
        if (userId != null) {
            // set userId to client
            client.set(UserConstants.USER_ID, userId);

            sessionService.saveSession(userId, client.getSessionId().toString());
            log.info("Client connected: {}", client.getSessionId());
        } else {
            log.info("Authentication failed for client: {}", client.getSessionId());
            client.disconnect();
        }
    }

    // on disconnect
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) throws ParseException, JOSEException {
        String userId = extractUserIdFromToken(client);
        if (userId != null) {
            sessionService.removeSession(userId, client.getSessionId().toString());
            log.info("Client disconnected: {}", client.getSessionId());
        }
    }

    // check authentication
    private String extractUserIdFromToken(SocketIOClient client) throws ParseException, JOSEException {
        String token = client.getHandshakeData().getSingleUrlParam(SecurityConstants.ACCESS_TOKEN_PARAM);

        if (token != null && !token.isBlank()) {
            // build introspect request
            IntrospectRequest introspectRequest = IntrospectRequest.builder()
                    .JwtToken(token)
                    .build();

            // verify token
            boolean isValid = authenticationService.introspect(introspectRequest).isValid();

            // return token if verify success
            if (isValid) {
                return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
            }
        }

        return null;
    }
}
