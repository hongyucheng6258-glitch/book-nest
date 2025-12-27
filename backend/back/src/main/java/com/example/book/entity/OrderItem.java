package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单详情表 实体类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_item")
public class OrderItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 订单详情ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 订单ID
     */
    private Integer orderId;
    
    /**
     * 书籍ID
     */
    private Integer bookId;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 单价
     */
    private BigDecimal price;
    
    // 关联书籍信息（非数据库字段）
    private Book book;
}