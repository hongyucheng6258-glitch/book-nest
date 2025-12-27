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
import java.util.List;

/**
 * 订单表 实体类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order")
public class Order implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 订单ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 总金额
     */
    private BigDecimal totalPrice;
    
    /**
     * 订单状态（0: 待付款, 1: 待发货, 2: 已发货, 3: 已完成, 4: 已取消）
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
    
    /**
     * 用户订单序号（每个用户的订单号从1开始计算）
     */
    private Integer userOrderNum;
    
    // 关联订单详情列表（非数据库字段）
    private List<OrderItem> orderItems;
    
    // 用户名（非数据库字段，用于前端显示）
    private String username;
}