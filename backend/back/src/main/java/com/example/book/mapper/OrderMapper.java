package com.example.book.mapper;

import com.example.book.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单表 Mapper 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 根据用户ID、订单ID、用户名和状态查询订单列表
     * @param userId 用户ID，可以为null表示查询所有用户
     * @param status 订单状态，可以为null表示查询所有状态
     * @param currentPage 当前页码
     * @param pageSize 每页条数
     * @return 订单列表
     */
    List<Order> selectByUserId(@Param("userId") Integer userId, @Param("id") Integer id, @Param("username") String username, @Param("status") Integer status, @Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize);
    
    /**
     * 查询订单总数，支持用户ID、订单ID、用户名和状态筛选
     * @param userId 用户ID，可以为null表示查询所有用户
     * @param status 订单状态，可以为null表示查询所有状态
     * @return 订单总数
     */
    int selectOrderCount(@Param("userId") Integer userId, @Param("id") Integer id, @Param("username") String username, @Param("status") Integer status);
    
    /**
     * 根据订单ID查询订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    Order selectById(@Param("id") Integer id);
    
    /**
     * 插入订单
     * @param order 订单信息
     * @return 影响行数
     */
    int insert(Order order);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 删除订单
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 获取今日订单数
     * @return 今日订单数
     */
    long getTodayOrderCount();
    
    /**
     * 获取本月销售额
     * @return 本月销售额
     */
    Double getMonthSales();
    
    /**
     * 获取用户当前最大订单序号
     * @param userId 用户ID
     * @return 最大订单序号
     */
    Integer getMaxUserOrderNum(@Param("userId") Integer userId);
    
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