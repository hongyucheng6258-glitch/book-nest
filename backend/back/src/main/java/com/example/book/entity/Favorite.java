package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("favorite")
public class Favorite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @Id(keyType = KeyType.Auto)
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
     * 创建时间
     */
    private LocalDateTime createdAt;

    // 关联书籍信息（非数据库字段）
    private Book book;
}