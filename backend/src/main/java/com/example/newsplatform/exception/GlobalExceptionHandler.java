package com.example.newsplatform.exception;

import com.example.newsplatform.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务校验异常（如：用户名已存在、密码错误等），返回 200 + code=-1 的 JSON，便于前端统一处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
    }
}
