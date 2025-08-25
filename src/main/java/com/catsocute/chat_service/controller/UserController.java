package com.catsocute.chat_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catsocute.chat_service.dto.request.UserCreationRequest;
import com.catsocute.chat_service.dto.response.ApiResponse;
import com.catsocute.chat_service.entity.User;
import com.catsocute.chat_service.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;

    // create user
    @PostMapping()
    ApiResponse<User> createUSer(@RequestBody @Valid UserCreationRequest request) {
        User user = userService.createUser(request);
        return ApiResponse.<User>builder()
            .result(user)
            .build();
    }

    // get all users
    @GetMapping()
    ApiResponse<List<User>> getUsers() {
        List<User> list = userService.getUsers();
        return ApiResponse.<List<User>>builder()
            .result(list)
            .build();
    }

    // get user by id
    @GetMapping("/{userId}")
    ApiResponse<User> getUser(@PathVariable("userId") String userId) {
        User user = userService.getUser(userId);
        return ApiResponse.<User>builder()
            .result(user)
            .build();
    }

    //get myinfo
    @GetMapping("/myinfo")
    ApiResponse<User> getMyInfo() {
        User user = userService.getMyInfo();
        log.info("Get info: ", user.getFullname());
        return ApiResponse.<User>builder()
            .result(user)
            .build();
    }

    // delete user by id
    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
            .result("Delete successfully!!!")
            .build();
    }

    // delete all user
    @DeleteMapping("/delete/all")
    ApiResponse<String> deleteAll() {
        userService.deleteAll();
        return ApiResponse.<String>builder()
            .result("Delete successfully!!!")
            .build();
    }

}
