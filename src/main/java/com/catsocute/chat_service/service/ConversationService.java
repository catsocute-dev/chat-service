package com.catsocute.chat_service.service;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.catsocute.chat_service.dto.request.ConversationRequest;
import com.catsocute.chat_service.dto.response.ConversationResponse;
import com.catsocute.chat_service.entity.Conversation;
import com.catsocute.chat_service.entity.ParticipantInfo;
import com.catsocute.chat_service.entity.User;
import com.catsocute.chat_service.exception.AppException;
import com.catsocute.chat_service.exception.ErrorCode;
import com.catsocute.chat_service.repository.ConversationRepository;
import com.catsocute.chat_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ConversationService {
    ConversationRepository conversationRepository;
    UserRepository userRepository;

    // create conversation
    public ConversationResponse createConversation(ConversationRequest request) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        String participantId = request.getParticipantIds().stream()
            .filter(userId -> !userId.equals(currentUserId))
            .findFirst()
            .orElse(null);
        List<String> userIds = new ArrayList<>();
        userIds.add(currentUserId);
        userIds.add(participantId);

        //get participantInfos
        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User participantInfo = userRepository.findById(participantId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        //get user ids hash
        String userIdHash = generateParticipantHash(userIds);

        Conversation conversation = (Conversation) conversationRepository.findByParticipantsHash(userIdHash)
            .orElseGet(() -> {
                List<ParticipantInfo> participantInfos = List.of(
                    ParticipantInfo.builder()
                        .userId(currentUser.getUserId())
                        .username(currentUser.getUsername())
                        .fullname(currentUser.getFullname())
                        .build(),
                    ParticipantInfo.builder()
                        .userId(participantInfo.getUserId())
                        .username(participantInfo.getUsername())
                        .fullname(participantInfo.getFullname())
                        .build()
                );

                //build conversation 
                Conversation newConversation = Conversation.builder()
                            .type(request.getType())
                            .participantsHash(userIdHash)
                            .createdDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .participants(participantInfos)
                            .build();
            
                return conversationRepository.save(newConversation);
            });

        return toConversationResponse(conversation);
    }

    // get my conversations
    public List<ConversationResponse> getConversations() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Conversation> conversations = conversationRepository.findAllByParticipantIdsContains(currentUserId);
        return conversations.stream()
            .map(conversation -> this.toConversationResponse(conversation)).toList();
    }

    //to conversation
    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationResponse conversationResponse = ConversationResponse.builder()
            .conversationId(conversation.getConversationId())
            .type(conversation.getType())
            .participantsHash(conversation.getParticipantsHash())
            .participants(conversation.getParticipants())
            .createdDate(Instant.now())
            .modifiedDate(Instant.now())
            .build();
        
        conversation.getParticipants().stream()
            .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
            .findFirst().ifPresent(participantInfo -> {
                conversationResponse.setConversationName(participantInfo.getFullname());
            });
        
        return conversationResponse;
    }

    // generate participantHash
    private String generateParticipantHash(List<String> userIds) {
        try {
            // sort
            Collections.sort(userIds);

            // join
            String raw = String.join("_", userIds);

            // hash HSA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodeHash = digest.digest(raw.getBytes());

            // convert to String
            StringBuffer hexString = new StringBuffer();
            for (byte b : encodeHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(raw);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new AppException(ErrorCode.GENERATION_ERROR);
        }
    }
}
