package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.Order;
import com.example.book.entity.User;
import com.example.book.mapper.UserMapper;
import com.example.book.service.OrderService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单表 Controller。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取订单列表
     *
     * @param userId 用户ID
     * @param id 订单ID
     * @param username 用户名
     * @param status 订单状态
     * @param currentPage 当前页码
     * @param pageSize 每页条数
     * @return 订单列表
     */
    @GetMapping("/list")
    public Result<com.example.book.common.PageResult<Order>> getOrderList(Integer userId, Integer id, String username, Integer status, Integer currentPage, Integer pageSize) {
        try {
            // 默认为第一页，每页10条
            if (currentPage == null) currentPage = 1;
            if (pageSize == null) pageSize = 10;
            
            // 从SecurityContext获取当前登录用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String usernameStr = userDetails.getUsername();
                
                // 根据用户名查询真实用户ID
                QueryWrapper queryWrapper = QueryWrapper.create().eq("username", usernameStr);
                User currentUser = userMapper.selectOneByQuery(queryWrapper);
                if (currentUser != null) {
                    Integer realUserId = currentUser.getId();
                    boolean isAdmin = userDetails.getAuthorities().stream()
                            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                    
                    // 非管理员只能查看自己的订单，覆盖前端传递的userId
                    if (!isAdmin) {
                        userId = realUserId;
                    }
                }
            }
            
            // 使用新方法查询订单总数，传递id参数支持订单ID搜索，传递username参数支持用户名搜索
            int total = orderService.getOrderCount(userId, id, username, status);
            
            // 查询分页数据，传递id参数支持订单ID搜索，传递username参数支持用户名搜索
            List<Order> orderList = orderService.getOrderListByUserId(userId, id, username, status, currentPage, pageSize);
            
            // 构造分页结果
            com.example.book.common.PageResult<Order> pageResult = new com.example.book.common.PageResult<>(currentPage, pageSize, Long.valueOf(total), orderList);
            
            // 返回分页结果
            return new Result<>(true, pageResult, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近7天销售额
     *
     * @return 最近7天销售额
     */
    @GetMapping("/last7days")
    public Result<List<java.util.Map<String, Object>>> getLast7DaysSales() {
        try {
            List<java.util.Map<String, Object>> salesData = orderService.getLast7DaysSales();
            return new Result<>(true, salesData, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取书籍分类占比
     *
     * @return 书籍分类占比
     */
    @GetMapping("/category-sales")
    public Result<List<java.util.Map<String, Object>>> getCategorySales() {
        try {
            List<java.util.Map<String, Object>> categoryData = orderService.getCategorySales();
            return new Result<>(true, categoryData, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            return new Result<>(true, order, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 创建的订单
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody Order order) {
        try {
            // 从SecurityContext中获取当前登录用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                throw new BusinessException("未登录或登录已过期");
            }
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // 输出日志
            System.out.println("当前登录用户名: " + username);
            System.out.println("前端传递的userId: " + order.getUserId());
            
            // 根据用户名查询真实用户ID
            QueryWrapper queryWrapper = QueryWrapper.create().eq("username", username);
            User currentUser = userMapper.selectOneByQuery(queryWrapper);
            if (currentUser == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 强制使用当前登录用户的真实ID，忽略前端传递的ID
            Integer realUserId = currentUser.getId();
            order.setUserId(realUserId);
            
            System.out.println("使用的真实用户ID: " + realUserId);
            
            // 添加详细日志
            System.out.println("开始创建订单，订单信息: " + order.toString());
            Order createdOrder = orderService.createOrder(order);
            System.out.println("订单创建成功，订单信息: " + createdOrder.toString());
            return new Result<>(true, createdOrder, "创建成功");
        } catch (Exception e) {
            // 添加详细日志
            System.out.println("订单创建失败，错误信息: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 订单状态
     * @return 操作结果
     */
    @PutMapping("/update/{id}")
    public Result<Integer> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam Integer status
    ) {
        try {
            int result = orderService.updateOrderStatus(id, status);
            return new Result<>(true, result, "更新成功");
        } catch (Exception e) {
            throw new BusinessException("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteOrder(@PathVariable Integer id) {
        try {
            int result = orderService.deleteOrder(id);
            return new Result<>(true, result, "删除成功");
        } catch (Exception e) {
            throw new BusinessException("删除失败: " + e.getMessage());
        }
    }
}