package com.catsocute.chat_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.catsocute.chat_service.dto.request.ChatMessageRequest;
import com.catsocute.chat_service.dto.response.ApiResponse;
import com.catsocute.chat_service.dto.response.ChatMessageResponse;
import com.catsocute.chat_service.service.ChatMessageService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatMessageController {
    ChatMessageService chatMessageService;

    @PostMapping("/create")
    ApiResponse<ChatMessageResponse> create(@RequestBody @Valid ChatMessageRequest request) {
        log.info("send message");
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ChatMessageResponse>> getMessages(
            @RequestParam("conversationId") String conversationId) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .result(chatMessageService.getMessages(conversationId))
                .build();
    }
}
