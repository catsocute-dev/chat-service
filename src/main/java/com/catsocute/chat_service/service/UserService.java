package com.catsocute.chat_service.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.catsocute.chat_service.dto.request.UserCreationRequest;
import com.catsocute.chat_service.entity.User;
import com.catsocute.chat_service.exception.AppException;
import com.catsocute.chat_service.exception.ErrorCode;
import com.catsocute.chat_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    //create user
    public User createUser(UserCreationRequest request) {
        //validate user existed
        boolean existed = userRepository.existsByUsername(request.getUsername());
        if(existed) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        User user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .fullname(request.getFullname())
        .build();
        
        return userRepository.save(user);
    }

    //get all users
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    //get user
    public User getUser(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    //get my info
    public User getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName(); //getName() -> jwt.getSubject

        //get current user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

        return user;
    }

    //delete user by id
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    //delete all users
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
