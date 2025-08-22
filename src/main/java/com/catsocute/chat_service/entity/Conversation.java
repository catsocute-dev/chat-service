package com.catsocute.chat_service.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @MongoId
    String conversationId;
    String type; //GROUP or DIRECT

    @Indexed(unique = true)
    String participantsHash;

    List<ParticipantInfo> participants;
    Instant createdDate;
    Instant modifiedDate;
}
