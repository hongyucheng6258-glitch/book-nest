package com.example.book.service;

import com.example.book.entity.Order;
import java.util.List;

/**
 * 订单表 Service 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface OrderService {
    
    /**
     * 根据用户ID、订单ID、用户名和状态查询订单列表
     *
     * @param userId 用户ID
     * @param id 订单ID
     * @param username 用户名
     * @param status 订单状态
     * @param currentPage 当前页码
     * @param pageSize 每页条数
     * @return 订单列表
     */
    List<Order> getOrderListByUserId(Integer userId, Integer id, String username, Integer status, Integer currentPage, Integer pageSize);
    
    /**
     * 查询订单总数
     *
     * @param userId 用户ID
     * @param id 订单ID
     * @param username 用户名
     * @param status 订单状态
     * @return 订单总数
     */
    int getOrderCount(Integer userId, Integer id, String username, Integer status);
    
    /**
     * 根据订单ID查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    Order getOrderById(Integer id);
    
    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 创建的订单
     */
    Order createOrder(Order order);
    
    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 订单状态
     * @return 影响行数
     */
    int updateOrderStatus(Integer id, Integer status);
    
    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteOrder(Integer id);
    
    /**
     * 获取最近7天销售额
     * @return 最近7天销售额列表
     */
    List<java.util.Map<String, Object>> getLast7DaysSales();
    
    /**
     * 获取书籍分类占比
     * @return 书籍分类占比列表
     */
    List<java.util.Map<String, Object>> getCategorySales();
}