package com.catsocute.chat_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catsocute.chat_service.dto.request.ConversationRequest;
import com.catsocute.chat_service.dto.response.ApiResponse;
import com.catsocute.chat_service.dto.response.ConversationResponse;
import com.catsocute.chat_service.service.ConversationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    ConversationService conversationService;

    @PostMapping("/create")
    ApiResponse<ConversationResponse> createConversation(@RequestBody ConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
            .result(conversationService.createConversation(request))
            .build();
    }

    @GetMapping("/mine")
    ApiResponse<List<ConversationResponse>> getConversations() {
        return ApiResponse.<List<ConversationResponse>>builder()
            .result(conversationService.getConversations())
            .build();
    }
}
