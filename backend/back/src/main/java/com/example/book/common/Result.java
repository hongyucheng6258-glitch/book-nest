package com.example.book.common;

import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private T data;
    private String message;

    // 构造器1：仅返回数据（默认成功）
    public Result(T data) {
        this.success = true;
        this.data = data;
    }

    // 构造器2：返回成功状态和提示信息
    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 构造器3：返回完整结果（状态、数据、提示）
    public Result(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}