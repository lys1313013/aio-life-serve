package com.lys.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.lys.core.constant.ResponseCodeConst;
import com.lys.core.resq.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author Lys
 * @date 2025/3/13
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        log.error("发生异常：{}", e.getMessage(), e);
        return ApiResponse.error(ResponseCodeConst.RSCODE_COMMON_FAIL, e.getMessage());
    }


    /**
     * 配合前端实现token失效时弹回登录页
     *
     * @param ex
     * @author Lys
     * @date 2025/3/13
     */
    @ExceptionHandler({NotLoginException.class})
    public ResponseEntity<String> handleUnauthorizedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("未授权: " + ex.getMessage());
    }
}
