package com.example.book.common;

import com.example.book.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        return new Result<>(false, null, ex.getMessage());
    }

    // 处理数据库异常
    @ExceptionHandler(SQLException.class)
    public Result<?> handleSQLException(SQLException ex) {
        return new Result<>(false, null, "数据库操作失败: " + ex.getMessage());
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException ex) {
        return new Result<>(false, null, "系统错误: 空指针异常");
    }

    // 处理请求方法不支持异常
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return new Result<>(false, null, "不支持的请求方法");
    }

    // 处理404异常
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new Result<>(false, null, "请求的接口不存在");
    }

    // 处理其他所有未捕获的异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        ex.printStackTrace(); // 打印异常堆栈信息到日志
        return new Result<>(false, null, "系统内部错误: " + ex.getMessage());
    }
}