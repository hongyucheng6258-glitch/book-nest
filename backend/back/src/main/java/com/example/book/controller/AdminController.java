package com.example.book.controller;

import com.example.book.entity.User;
import com.example.book.mapper.BookMapper;
import com.example.book.mapper.OrderMapper;
import com.example.book.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取仪表盘统计数据
     * @return 统计数据
     */
    @GetMapping("/dashboard/stats")
    public com.example.book.common.Result<java.util.Map<String, Object>> getDashboardStats() {
        // 1. 获取真实的书籍总数
        long bookCount = bookMapper.selectTotalCount();

        // 2. 获取真实的用户总数
        long userCount = userMapper.selectCountByQuery(QueryWrapper.create());

        // 3. 获取真实的今日订单数
        long todayOrderCount = orderMapper.getTodayOrderCount();

        // 4. 获取真实的本月销售额（处理可能的null值）
        Double monthSalesDouble = orderMapper.getMonthSales();
        double monthSales = monthSalesDouble != null ? monthSalesDouble : 0.0;
        
        // 调试信息
        System.out.println("仪表盘统计数据调试:");
        System.out.println("书籍总数: " + bookCount);
        System.out.println("用户总数: " + userCount);
        System.out.println("今日订单数: " + todayOrderCount);
        System.out.println("本月销售额（原始值）: " + monthSalesDouble);
        System.out.println("本月销售额（处理后）: " + monthSales);

        // 5. 组装结果
        Map<String, Object> data = Map.of(
            "bookCount", bookCount,
            "userCount", userCount,
            "todayOrderCount", todayOrderCount,
            "monthSales", monthSales
        );

        return new com.example.book.common.Result<>(true, data, "获取统计数据成功");
    }
}
