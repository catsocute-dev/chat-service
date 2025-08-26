# Chat Service - Real-time Chat Application
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-6.0+-green.svg)](https://www.mongodb.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0+-red.svg)](https://redis.io/)
[![Netty Socket.IO](https://img.shields.io/badge/NettySocket.IO-2.0.13-yellow.svg)]([https://socket.io/](https://github.com/mrniko/netty-socketio))
## 📋 Overview

Chat Service is a real-time chat application built with Spring Boot, supporting instant communication between users through WebSocket and REST API. The application adopts a hybrid database architecture (MySQL + MongoDB) and uses Redis for session management.

## 🏗️ Architecture

### **Technology Stack**
- **Backend**: Spring Boot 3.5.4, Java 21
- **Database**: 
  - MySQL (User management)
  - MongoDB (Chat messages & Conversations)
- **Cache & Session**: Redis
- **Real-time**: Socket.IO (Netty)
- **Security**: JWT Authentication, Spring Security
- **Build Tool**: Maven

### **System Architecture**

```
                        ┌─────────────────┐    
                        │     Clients     │    
                        └─────────┬───────┘    
                                  │            
                                  │
                    ┌─────────────┴─────────────┐
                    │     Spring Boot App       │
                    │  ┌─────────────────────┐  │
                    │  │   REST Controllers  │  │
                    │  └─────────┬───────────┘  │
                    │  ┌─────────┴───────────┐  │
                    │  │   Socket.IO Server  │  │
                    │  └─────────┬───────────┘  │
                    │  ┌─────────┴───────────┐  │
                    │  │   Business Services │  │
                    │  └─────────┬───────────┘  │
                    │  ┌─────────┴───────────┐  │
                    │  │   Repositories      │  │
                    │  └─────────┬───────────┘  │
                    └────────────┼──────────────┘
                                 │
          ┌──────────────────────┼─────────────────────┐
          │                      │                     │
    ┌─────▼─────┐        ┌───────▼──────┐        ┌─────▼─────┐
    │   MySQL   │        │   MongoDB    │        │   Redis   │
    │  (Users)  │        │(Messages &   │        │(Sessions) │
    │           │        │Conversations)│        │           │
    └───────────┘        └──────────────┘        └───────────┘
```

## 🔐 Authentication & Session Management

### **JWT Authentication Flow**
1. User logs in with username/password
2. Server validates the credentials and generates a JWT token
3. Client uses the JWT token for API calls
4. The Socket.IO connection also uses the JWT token for authentication

### **Redis Session Management**

Redis is used to manage user sessions:

#### **Session Storage Structure**
```
Key: user:session:{userId}
Type: Set
Value: [sessionId1, sessionId2, ...]
TTL: 24 hours
```

#### **Session Operations**

```java
// Save session
public void saveSession(String userId, String sessionId) {
    String key = RedisKeys.USER_SESSION_KEY + userId;
    redisTemplate.opsForSet().add(key, sessionId);
    redisTemplate.expire(key, Duration.ofHours(24));
}

// Get all sessions for a user
public Set<String> getSessions(String userId) {
    String key = RedisKeys.USER_SESSION_KEY + userId;
    return redisTemplate.opsForSet().members(key);
}

// Remove specific session
public void removeSession(String userId, String sessionId) {
    String key = RedisKeys.USER_SESSION_KEY + userId;
    redisTemplate.opsForSet().remove(key, sessionId);
}
```

#### **Benefits của Redis Session Management**
- **Multi-device Support**: A user can be online on multiple devices
- **Real-time Presence**: Track which users are currently online
- **Efficient Broadcasting**: Send messages only to online users
- **Automatic Cleanup**: TTL automatically removes expired sessions

## 💬 Real-time Messaging

### **Socket.IO Integration**

#### **Connection Flow**
1. Client connects with a JWT token in the URL parameter
2. Server validates the token and extracts the userId
3. Register the session in Redis
4. Client can receive/send messages

#### **Message Broadcasting**
```java
// Optimized broadcasting to conversation participants
Set<String> sessionIds = getSessionIds(conversationId);
socketIOServer.getAllClients().forEach(client -> {
    if(sessionIds.contains(client.getSessionId().toString())) {
        client.sendEvent(SocketEvent.CHAT_MESSAGE, message);
    }
});
```


## 🚀 API Endpoints

### **Authentication**
```
POST /auth/log-in          - User login
POST /users                - User registration
```

### **Conversations**
```
POST /conversations        - Create conversation
GET  /conversations        - Get user conversations
```

### **Messages**
```
POST /messages/create      - Send message
GET  /messages?conversationId={id} - Get conversation messages
```

### **Socket.IO Events**
```
Event: 'connect'           - Client connection
Event: 'disconnect'        - Client disconnection
Event: 'chat_message'      - Real-time message
```

## ⚙️ Configuration

### **Application Properties**
```yaml
# Database
spring:
  datasource:
    url: <your_mysql_url>
    username: root
    password: <your_mysql_password>
  data:
    mongodb:
      uri: <your_mongodb_uri>

# Redis
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:<default_password>}
      timeout: 6000
      lettuce:
        pool:
          max-active: 20     
          max-idle: 10
          min-idle: 2
          time-between-eviction-runs: 10000

# Security
security:
  jwt:
    signer-key: <your-secret-key-here>

```
