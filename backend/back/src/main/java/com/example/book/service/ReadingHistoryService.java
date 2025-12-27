package com.example.book.service;

import com.example.book.entity.ReadingHistory;

import java.util.List;

/**
 * 阅读历史记录 Service 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface ReadingHistoryService {
    
    /**
     * 添加或更新阅读记录
     * @param readingHistory 阅读记录对象
     * @return 操作结果
     */
    boolean addOrUpdateHistory(ReadingHistory readingHistory);
    
    /**
     * 根据用户ID获取阅读历史记录
     * @param userId 用户ID
     * @return 阅读历史记录列表
     */
    List<ReadingHistory> getReadingHistory(Integer userId);
    
    /**
     * 根据阅读记录ID删除阅读记录
     * @param id 阅读记录ID
     * @return 操作结果
     */
    boolean deleteHistory(Integer id);
    
    /**
     * 清空用户阅读历史记录
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean clearHistory(Integer userId);
    
    /**
     * 根据用户ID获取阅读历史数量
     * @param userId 用户ID
     * @return 阅读历史数量
     */
    int getHistoryCount(Integer userId);
}