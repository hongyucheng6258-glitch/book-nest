package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.Cart;
import com.example.book.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车表 Controller。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 获取购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> getCartList(@RequestParam Integer userId) {
        try {
            List<Cart> cartList = cartService.getCartListByUserId(userId);
            return new Result<>(true, cartList, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加商品到购物车
     *
     * @param userId   用户ID
     * @param bookId   书籍ID
     * @param quantity 数量
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Integer> addToCart(
            @RequestParam Integer userId,
            @RequestParam Integer bookId,
            @RequestParam(defaultValue = "1") Integer quantity
    ) {
        try {
            int result = cartService.addToCart(userId, bookId, quantity);
            return new Result<>(true, result, "添加成功");
        } catch (Exception e) {
            throw new BusinessException("添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新购物车商品数量
     *
     * @param id       购物车ID
     * @param quantity 数量
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<Integer> updateCartItemQuantity(
            @RequestParam Integer id,
            @RequestParam Integer quantity
    ) {
        try {
            int result = cartService.updateCartItemQuantity(id, quantity);
            return new Result<>(true, result, "更新成功");
        } catch (Exception e) {
            throw new BusinessException("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 移除购物车商品
     *
     * @param id 购物车ID
     * @return 操作结果
     */
    @DeleteMapping("/remove/{id}")
    public Result<Integer> removeCartItem(@PathVariable Integer id) {
        try {
            int result = cartService.removeCartItem(id);
            return new Result<>(true, result, "移除成功");
        } catch (Exception e) {
            throw new BusinessException("移除失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Result<Integer> clearCart(@RequestParam Integer userId) {
        try {
            int result = cartService.clearCartByUserId(userId);
            return new Result<>(true, result, "清空成功");
        } catch (Exception e) {
            throw new BusinessException("清空失败: " + e.getMessage());
        }
    }
}