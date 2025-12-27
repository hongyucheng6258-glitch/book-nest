package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书籍表 实体类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("book")
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 书籍ID
     */
    @Id(keyType = KeyType.None)
    private Integer id;

    /**
     * 书名
     */
    private String name;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 作者
     */
    private String author;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 所属分类ID（关联category表）
     */
    private Integer categoryId;
    
    /**
     * 书籍图片URL
     */
    private String imageUrl;

    /**
     * 书籍详情描述
     */
    private String description;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 已卖数量
     */
    private Integer soldCount;

}
