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
 * 评分表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("rating")
public class Rating implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 评分ID
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
     * 评分（1-5）
     */
    private Double rating;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    // 用户名（非数据库字段，用于显示）
    private String userName;
    
    // 书籍名称（非数据库字段，用于显示）
    private String bookName;
}
