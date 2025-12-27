package com.example.book.service.impl;

import com.example.book.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 购物车清理服务
 * 定时清理过期的购物车商品
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class CartCleanupService {
    
    @Autowired
    private CartMapper cartMapper;
    
    /**
     * 每天凌晨1点执行一次，清理过期购物车商品
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredCarts() {
        System.out.println("Starting to clean expired carts...");
        int deletedCount = cartMapper.deleteExpired();
        System.out.println("Cleaned " + deletedCount + " expired cart items.");
    }
}