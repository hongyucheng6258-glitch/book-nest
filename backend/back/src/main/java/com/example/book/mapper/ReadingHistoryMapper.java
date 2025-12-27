package com.example.book.mapper;

import com.example.book.entity.ReadingHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阅读历史记录 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface ReadingHistoryMapper {
    
    /**
     * 添加或更新阅读记录
     * @param readingHistory 阅读记录对象
     * @return 影响行数
     */
    int addOrUpdateHistory(ReadingHistory readingHistory);
    
    /**
     * 根据用户ID获取阅读历史记录
     * @param userId 用户ID
     * @return 阅读历史记录列表
     */
    List<ReadingHistory> getReadingHistoryByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据阅读记录ID删除阅读记录
     * @param id 阅读记录ID
     * @return 影响行数
     */
    int deleteHistoryById(@Param("id") Integer id);
    
    /**
     * 清空用户阅读历史记录
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteAllHistoryByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID获取阅读历史数量
     * @param userId 用户ID
     * @return 阅读历史数量
     */
    int getHistoryCountByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和书籍ID获取阅读记录
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 阅读记录对象
     */
    ReadingHistory getHistoryByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);
}