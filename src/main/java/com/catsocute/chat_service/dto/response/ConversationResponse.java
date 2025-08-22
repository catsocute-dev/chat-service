package com.catsocute.chat_service.dto.response;

import java.time.Instant;
import java.util.List;

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
public class ConversationResponse {
    String conversationId;
    String type; //GROUP or DIRECT
    String participantsHash;
    String conversationName;
    List<ParticipantInfo> participants;
    Instant createdDate;
    Instant modifiedDate;
}
