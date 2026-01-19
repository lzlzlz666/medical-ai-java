package com.lz.handler;

import com.lz.constant.MessageConstant;
import com.lz.exception.BaseException;
import com.lz.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理 Spring Boot 参数校验失败的异常
     * 当 @Validated 校验失败时，会抛出 MethodArgumentNotValidException
     */
    @ExceptionHandler
    public Result exceptionHandler(MethodArgumentNotValidException ex) {
        log.error("参数校验异常信息：{}", ex.getMessage());

        // 1. 获取校验结果
        BindingResult bindingResult = ex.getBindingResult();

        // 2. 获取第一条错误信息（通常只需要返回第一条给前端）
        String message = bindingResult.getFieldError().getDefaultMessage();

        // 3. 返回错误结果给前端
        return Result.error(message);
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ACCOUNT_ALREADY_EXIST;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}