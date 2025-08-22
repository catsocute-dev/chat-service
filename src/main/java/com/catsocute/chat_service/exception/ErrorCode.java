package com.catsocute.chat_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXISTED(1001, "Username existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User not existed", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_EXISTED(1005, "Username not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_EXISTED(1008, "Role not existed", HttpStatus.NOT_FOUND),
    GENERATION_ERROR(1009, "Error generating hash", HttpStatus.BAD_REQUEST)

    ;

    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
