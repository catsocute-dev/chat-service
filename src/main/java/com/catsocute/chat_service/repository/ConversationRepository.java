package com.catsocute.chat_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.catsocute.chat_service.entity.Conversation;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String>{
    Optional<Conversation> findByParticipantsHash(String userIdsHash);

    @Query("{'participants.userId' : ?0}")
    List<Conversation> findAllByParticipantIdsContains(String currentUserId);
}
