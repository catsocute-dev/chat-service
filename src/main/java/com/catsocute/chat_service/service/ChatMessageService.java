package com.catsocute.chat_service.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.catsocute.chat_service.constant.SocketEvent;
import com.catsocute.chat_service.dto.request.ChatMessageRequest;
import com.catsocute.chat_service.dto.response.ChatMessageResponse;
import com.catsocute.chat_service.entity.ChatMessage;
import com.catsocute.chat_service.entity.ParticipantInfo;
import com.catsocute.chat_service.exception.AppException;
import com.catsocute.chat_service.exception.ErrorCode;
import com.catsocute.chat_service.repository.ChatMessageRepository;
import com.catsocute.chat_service.repository.ConversationRepository;
import com.corundumstudio.socketio.SocketIOServer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {

    ConversationRepository conversationRepository;
    ChatMessageRepository chatMessageRepository;
    SocketIOServer socketIOServer;
    SessionService sessionService;

    // create chat message
    public ChatMessageResponse create(ChatMessageRequest request) {
        // validate conversation
        ParticipantInfo participantInfo = validateParticipantInfo(request.getConversationId());

        // build chat message info
        ChatMessage chatMessage = ChatMessage.builder()
                .conversationId(request.getConversationId())
                .message(request.getMessage())
                .sender(participantInfo)
                .createDate(Instant.now())
                .build();

        // create chat message
        chatMessage = chatMessageRepository.save(chatMessage);
        String message = chatMessage.getMessage();

        Set<String> sessionIds = getSessionIds(request.getConversationId());

        //Public socket IO to clients
        socketIOServer.getAllClients().forEach(client -> {
            if(sessionIds.contains(client.getSessionId().toString())) {
                client.sendEvent(SocketEvent.CHAT_MESSAGE, message);
            }
        });

        // convert to chatMessageResponse
        return toChatMessageResponse(chatMessage);
    }

    // get messages
    public List<ChatMessageResponse> getMessages(String conversationId) {
        // validate conversation
        validateParticipantInfo(conversationId);
        // get chat message
        List<ChatMessage> messages = chatMessageRepository.findAllByConversationIdOrderByCreateDateDesc(conversationId);

        return messages.stream().map(chatMessage -> this.toChatMessageResponse(chatMessage)).toList();
    }

    // to chatMessageResponse
    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        String currentUserId = getCurrentUserId();
        boolean isMe = currentUserId.equals(chatMessage.getSender().getUserId());

        return ChatMessageResponse.builder()
                .messageId(chatMessage.getMessageId())
                .conversationId(chatMessage.getConversationId())
                .isMe(isMe)
                .message(chatMessage.getMessage())
                .sender(chatMessage.getSender())
                .createDate(chatMessage.getCreateDate())
                .build();
    }

    // validate conversation
    private ParticipantInfo validateParticipantInfo(String conversationId) {
        String currentUserId = getCurrentUserId();
        return conversationRepository.findById(conversationId) // validate conversationId
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants() // validate participantInfo
                .stream()
                .filter(participant -> currentUserId.equals(participant.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_PARTICIPANT));
    }

    //get current userId
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    //get sessionIds
    private Set<String> getSessionIds(String conversationId) {
        //get users in coversation
        List<String> userIds = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
            .getParticipants()
            .stream()
            .map(p -> p.getUserId())
            .toList();

        Set<String> sessionIds = userIds.stream()
            .map(userId -> sessionService.getSessions(userId))
            .filter(Objects::nonNull)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

        return sessionIds;
    }
}
