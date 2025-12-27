package com.example.book.service;

import com.example.book.entity.Cart;
import java.util.List;

/**
 * 购物车表 Service 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface CartService {
    
    /**
     * 根据用户ID查询购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> getCartListByUserId(Integer userId);
    
    /**
     * 添加商品到购物车
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param quantity 数量
     * @return 影响行数
     */
    int addToCart(Integer userId, Integer bookId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     *
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 影响行数
     */
    int updateCartItemQuantity(Integer cartId, Integer quantity);
    
    /**
     * 移除购物车商品
     *
     * @param cartId 购物车ID
     * @return 影响行数
     */
    int removeCartItem(Integer cartId);
    
    /**
     * 根据用户ID清空购物车
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int clearCartByUserId(Integer userId);
}