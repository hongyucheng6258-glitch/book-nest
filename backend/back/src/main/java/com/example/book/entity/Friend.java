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
 * 好友关系表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("friend")
public class Friend implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 好友关系ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 用户1ID
     */
    private Integer user1Id;
    
    /**
     * 用户2ID
     */
    private Integer user2Id;
    
    /**
     * 关系状态：1-正常，2-已删除
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    // 关联用户信息（非数据库字段）
    private User user1;
    private User user2;
    
    // 用户1和用户2的用户名（非数据库字段，用于显示）
    private String username1;
    private String username2;
    
    // 用户1和用户2的头像（非数据库字段，用于显示）
    private String avatar1;
    private String avatar2;
    
    // 好友用户名（非数据库字段，用于显示）
    private String friendUsername;
}