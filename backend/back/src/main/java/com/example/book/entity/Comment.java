package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("comment")
@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Comment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 评论ID
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
     * 评论内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 父评论ID，用于嵌套回复
     */
    private Integer parentId;
    
    /**
     * 点赞数量
     */
    private Integer likeCount;
    
    // 关联用户信息（非数据库字段）
    private User user;
    
    // 用户名（非数据库字段，用于显示）
    private String userName;
    
    // 书籍名称（非数据库字段，用于显示）
    private String bookName;
    
    // 子评论列表（非数据库字段）
    private List<Comment> replies;
    
    // 是否已点赞（非数据库字段）
    private Boolean isLiked;
}
