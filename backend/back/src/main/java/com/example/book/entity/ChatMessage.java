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
 * 聊天消息表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_message")
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
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
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    // 关联用户信息（非数据库字段）
    private User fromUser;
    private User toUser;
    
    // 发送者用户名（非数据库字段，用于显示）
    private String fromUsername;
    // 接收者用户名（非数据库字段，用于显示）
    private String toUsername;
}