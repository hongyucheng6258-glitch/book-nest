package com.example.book.common;

import lombok.Data;

import java.util.List;

/**
 * 订单分页结果类
 * 用于返回包含分页信息的订单列表
 */
@Data
public class OrderPageResult<T> {
    private List<T> records; // 订单列表
    private int total; // 总条数
    private int currentPage; // 当前页码
    private int pageSize; // 每页条数

    public OrderPageResult(List<T> records, int total, int currentPage, int pageSize) {
        this.records = records;
        this.total = total;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}