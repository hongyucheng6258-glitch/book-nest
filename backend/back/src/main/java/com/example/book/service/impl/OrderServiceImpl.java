package com.example.book.service.impl;

import com.example.book.entity.Order;
import com.example.book.entity.OrderItem;
import com.example.book.entity.Book;
import com.example.book.mapper.OrderMapper;
import com.example.book.mapper.OrderItemMapper;
import com.example.book.service.OrderService;
import com.example.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单表 Service 实现类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private com.example.book.service.CartService cartService;
    
    @Autowired
    private com.example.book.service.BookService bookService;
    
    /**
     * 根据用户ID和状态查询订单列表
     */
    @Override
    public List<Order> getOrderListByUserId(Integer userId, Integer id, String username, Integer status, Integer currentPage, Integer pageSize) {
        // 使用传统MyBatis的selectByUserId方法，传递id和username参数支持订单ID和用户名搜索
        return orderMapper.selectByUserId(userId, id, username, status, currentPage, pageSize);
    }
    
    /**
     * 查询订单总数
     */
    @Override
    public int getOrderCount(Integer userId, Integer id, String username, Integer status) {
        // 使用传统MyBatis的selectOrderCount方法，传递id和username参数支持订单ID和用户名搜索
        return orderMapper.selectOrderCount(userId, id, username, status);
    }
    
    /**
     * 根据订单ID查询订单详情
     */
    @Override
    public Order getOrderById(Integer id) {
        // 使用传统MyBatis的selectById方法
        return orderMapper.selectById(id);
    }
    
    /**
     * 创建订单（包含订单详情）
     */
    @Override
    @Transactional
    public Order createOrder(Order order) {
        // 1. 检查库存是否充足
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                Integer bookId = item.getBookId();
                Integer quantity = item.getQuantity();
                
                // 查询书籍详情，获取书籍名称
                Book book = bookService.getBookById(bookId);
                if (book == null) {
                    throw new RuntimeException("书籍不存在：" + bookId);
                }
                
                // 检查库存
                boolean isStockEnough = bookService.checkStock(bookId, quantity);
                if (!isStockEnough) {
                    throw new RuntimeException("书籍库存不够：" + book.getName());
                }
            }
        }
        
        // 2. 计算用户订单序号，每个用户从1开始
        Integer maxUserOrderNum = orderMapper.getMaxUserOrderNum(order.getUserId());
        Integer userOrderNum = (maxUserOrderNum == null ? 0 : maxUserOrderNum) + 1;
        order.setUserOrderNum(userOrderNum);
        
        // 3. 插入订单主表（使用传统MyBatis的insert方法）
        orderMapper.insert(order);
        
        // 4. 插入订单详情表并扣减库存
        if (orderItems != null && !orderItems.isEmpty()) {
            // 设置订单ID
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getId());
                
                // 扣减库存
                bookService.decreaseStock(item.getBookId(), item.getQuantity());
            }
            orderItemMapper.batchInsert(orderItems);
        }
        
        // 5. 订单创建成功后清空购物车
        cartService.clearCartByUserId(order.getUserId());
        
        // 6. 返回包含订单详情的完整订单
        return orderMapper.selectById(order.getId());
    }
    
    /**
     * 更新订单状态
     */
    @Override
    public int updateOrderStatus(Integer id, Integer status) {
        // 使用传统MyBatis的updateStatus方法
        return orderMapper.updateStatus(id, status);
    }
    
    /**
     * 删除订单（包含订单详情）
     */
    @Override
    public int deleteOrder(Integer id) {
        // 删除订单详情
        orderItemMapper.deleteByOrderId(id);
        // 删除订单主表（使用传统MyBatis的deleteById方法）
        return orderMapper.deleteById(id);
    }
    
    /**
     * 获取最近7天销售额
     */
    @Override
    public List<java.util.Map<String, Object>> getLast7DaysSales() {
        // 使用传统MyBatis的getLast7DaysSales方法
        return orderMapper.getLast7DaysSales();
    }
    
    /**
     * 获取书籍分类占比
     */
    @Override
    public List<java.util.Map<String, Object>> getCategorySales() {
        // 使用传统MyBatis的getCategorySales方法
        return orderMapper.getCategorySales();
    }
}