package com.example.book.service;

import com.example.book.entity.Favorite;

import java.util.List;

/**
 * 收藏表 Service 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface FavoriteService {
    // 根据用户ID查询收藏列表
    List<Favorite> getFavoriteListByUserId(Integer userId);

    // 根据用户ID和书籍ID查询收藏
    Favorite getFavoriteByUserIdAndBookId(Integer userId, Integer bookId);

    // 添加收藏
    int addFavorite(Integer userId, Integer bookId);

    // 取消收藏
    int removeFavorite(Integer userId, Integer bookId);

    // 检查是否已收藏
    boolean isFavorited(Integer userId, Integer bookId);
}