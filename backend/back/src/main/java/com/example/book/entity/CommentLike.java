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
 * 评论点赞表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("comment_like")
public class CommentLike implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 点赞ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 评论ID
     */
    private Integer commentId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}