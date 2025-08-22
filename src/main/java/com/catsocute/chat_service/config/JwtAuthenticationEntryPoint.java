package com.catsocute.chat_service.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.catsocute.chat_service.dto.response.ApiResponse;
import com.catsocute.chat_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{

    @Override
    public void commence(HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException exception) throws IOException, ServletException {

            ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
            ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

            ObjectMapper objectMapper = new ObjectMapper();

            response.setStatus(errorCode.getHttpStatusCode().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            response.flushBuffer(); // send to client
    }
}
