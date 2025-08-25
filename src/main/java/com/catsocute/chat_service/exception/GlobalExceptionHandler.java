package com.catsocute.chat_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.catsocute.chat_service.dto.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
            .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
            .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
            .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = ApiResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    //handling Denied Access
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedEXception(AccessDeniedException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
            .code(ErrorCode.UNAUTHORIZED.getCode())
            .message(ErrorCode.UNAUTHORIZED.getMessage())
            .build();
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatusCode()).body(apiResponse);
    }

    //handling MethodArgumentNotValidException
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        ApiResponse apiResponse = ApiResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
