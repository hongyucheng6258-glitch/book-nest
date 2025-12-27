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
 * 好友请求表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("friend_request")
public class FriendRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 请求ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 发送者用户ID
     */
    private Integer fromUserId;
    
    /**
     * 接收者用户ID
     */
    private Integer toUserId;
    
    /**
     * 请求状态：1-待处理，2-已接受，3-已拒绝
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
    private User fromUser;
    private User toUser;
    
    // 发送者用户名（非数据库字段，用于显示）
    private String fromUsername;
    // 接收者用户名（非数据库字段，用于显示）
    private String toUsername;
}