package com.catsocute.chat_service.service;

import java.time.Duration;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.catsocute.chat_service.constant.RedisKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {
    StringRedisTemplate redisTemplate;

    /**
     * save session for user
     */
    public void saveSession(String userId, String sessionId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        redisTemplate.opsForSet().add(key, sessionId);
        redisTemplate.expire(key, Duration.ofHours(24)); //set time to live = 24h
    }

    /**
     * get sessions
     */
    public Set<String> getSessions(String userId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * delete session
     */
    public void removeSession(String userId, String sessionId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        redisTemplate.opsForSet().remove(key, sessionId);
    }
}
