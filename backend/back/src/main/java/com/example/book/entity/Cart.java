package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车表 实体类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("cart")
public class Cart implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 购物车ID
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
     * 数量
     */
    private Integer quantity;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
    
    // 关联书籍信息（非数据库字段）
    private Book book;
}