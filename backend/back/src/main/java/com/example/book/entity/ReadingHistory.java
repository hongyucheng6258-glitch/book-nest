package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 阅读历史记录 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reading_history")
public class ReadingHistory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 阅读记录ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 书籍ID
     */
    private Integer bookId;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readAt;
    
    /**
     * 最后阅读页码
     */
    private Integer lastPage;
    
    /**
     * 阅读进度（百分比）
     */
    private Integer readingProgress;
    
    // 书籍信息（非数据库字段，用于前端显示）
    private Book book;
}