package com.example.book.mapper;

import com.example.book.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单详情表 Mapper 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface OrderItemMapper {
    
    /**
     * 根据订单ID查询订单详情列表
     *
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Integer orderId);
    
    /**
     * 批量插入订单详情
     *
     * @param orderItems 订单详情列表
     * @return 影响行数
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);
    
    /**
     * 根据订单ID删除订单详情
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(@Param("orderId") Integer orderId);
}