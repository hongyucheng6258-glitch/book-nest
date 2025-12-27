package com.example.book.service.impl;

import com.example.book.entity.Favorite;
import com.example.book.mapper.FavoriteMapper;
import com.example.book.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏表 Service 实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 根据用户ID查询收藏列表
     */
    @Override
    public List<Favorite> getFavoriteListByUserId(Integer userId) {
        return favoriteMapper.selectByUserId(userId);
    }

    /**
     * 根据用户ID和书籍ID查询收藏
     */
    @Override
    public Favorite getFavoriteByUserIdAndBookId(Integer userId, Integer bookId) {
        return favoriteMapper.selectByUserIdAndBookId(userId, bookId);
    }

    /**
     * 添加收藏
     */
    @Override
    public int addFavorite(Integer userId, Integer bookId) {
        Favorite favorite = Favorite.builder()
                .userId(userId)
                .bookId(bookId)
                .build();
        return favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     */
    @Override
    public int removeFavorite(Integer userId, Integer bookId) {
        return favoriteMapper.deleteByUserIdAndBookId(userId, bookId);
    }

    /**
     * 检查是否已收藏
     */
    @Override
    public boolean isFavorited(Integer userId, Integer bookId) {
        Favorite favorite = favoriteMapper.selectByUserIdAndBookId(userId, bookId);
        return favorite != null;
    }
}