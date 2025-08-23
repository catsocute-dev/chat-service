package com.catsocute.chat_service.dto.response;

import java.time.Instant;

import com.catsocute.chat_service.entity.ParticipantInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatMessageResponse {
    String messageId;
    String conversationId;
    Boolean isMe;
    String message;
    ParticipantInfo sender;
    Instant createDate;
}
