package com.example.book.mapper;

import com.example.book.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车表 Mapper 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface CartMapper {
    
    /**
     * 根据用户ID查询购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和书籍ID查询购物车项
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 购物车项
     */
    Cart selectByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);
    
    /**
     * 插入购物车项
     *
     * @param cart 购物车项
     * @return 影响行数
     */
    int insert(Cart cart);
    
    /**
     * 更新购物车项数量
     *
     * @param id       购物车ID
     * @param quantity 数量
     * @return 影响行数
     */
    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);
    
    /**
     * 删除购物车项
     *
     * @param id 购物车ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据用户ID清空购物车
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID获取未过期的购物车商品列表
     *
     * @param userId 用户ID
     * @return 未过期的购物车商品列表
     */
    List<Cart> selectValidByUserId(@Param("userId") Integer userId);
    
    /**
     * 删除过期购物车商品
     *
     * @return 影响行数
     */
    int deleteExpired();
    
    /**
     * 更新购物车商品过期时间
     *
     * @param id 购物车商品ID
     * @param expireAt 过期时间
     * @return 影响行数
     */
    int updateExpireTime(@Param("id") Integer id, @Param("expireAt") java.time.LocalDateTime expireAt);
}