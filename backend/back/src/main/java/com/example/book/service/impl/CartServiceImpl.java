package com.example.book.service.impl;

import com.example.book.entity.Cart;
import com.example.book.mapper.CartMapper;
import com.example.book.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车表 Service 实现类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    /**
     * 根据用户ID查询购物车列表
     */
    @Override
    public List<Cart> getCartListByUserId(Integer userId) {
        return cartMapper.selectValidByUserId(userId);
    }
    
    /**
     * 添加商品到购物车
     */
    @Override
    public int addToCart(Integer userId, Integer bookId, Integer quantity) {
        System.out.println("addToCart called with userId: " + userId + ", bookId: " + bookId + ", quantity: " + quantity);
        // 设置购物车商品过期时间为30天后
        LocalDateTime expireAt = LocalDateTime.now().plusDays(30);
        
        // 检查购物车中是否已存在该商品
        Cart existingCart = cartMapper.selectByUserIdAndBookId(userId, bookId);
        System.out.println("Existing cart: " + existingCart);
        
        if (existingCart != null) {
            // 如果已存在，更新数量和过期时间
            int newQuantity = existingCart.getQuantity() + quantity;
            System.out.println("Updating quantity to: " + newQuantity);
            // 更新过期时间
            existingCart.setExpireAt(expireAt);
            int result = cartMapper.updateQuantity(existingCart.getId(), newQuantity);
            // 刷新过期时间
            cartMapper.updateExpireTime(existingCart.getId(), expireAt);
            System.out.println("Update result: " + result);
            return result;
        } else {
            // 如果不存在，添加新的购物车项
            Cart cart = Cart.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .quantity(quantity)
                    .expireAt(expireAt)
                    .build();
            System.out.println("Inserting new cart: " + cart);
            int result = cartMapper.insert(cart);
            System.out.println("Insert result: " + result);
            return result;
        }
    }
    
    /**
     * 更新购物车商品数量
     */
    @Override
    public int updateCartItemQuantity(Integer cartId, Integer quantity) {
        // 更新数量
        int result = cartMapper.updateQuantity(cartId, quantity);
        // 刷新过期时间为30天后
        LocalDateTime expireAt = LocalDateTime.now().plusDays(30);
        cartMapper.updateExpireTime(cartId, expireAt);
        return result;
    }
    
    /**
     * 移除购物车商品
     */
    @Override
    public int removeCartItem(Integer cartId) {
        return cartMapper.deleteById(cartId);
    }
    
    /**
     * 根据用户ID清空购物车
     */
    @Override
    public int clearCartByUserId(Integer userId) {
        return cartMapper.deleteByUserId(userId);
    }
}