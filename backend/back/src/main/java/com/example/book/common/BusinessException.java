package com.example.book.common;

/**
 * 自定义业务异常类
 * 用于表示业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}