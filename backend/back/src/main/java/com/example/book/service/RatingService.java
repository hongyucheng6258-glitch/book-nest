package com.example.book.service;

import com.example.book.entity.Rating;

import java.util.List;

/**
 * 评分服务接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface RatingService {
    
    /**
     * 添加或更新评分
     * @param rating 评分对象
     * @return 添加或更新结果
     */
    boolean addOrUpdateRating(Rating rating);
    
    /**
     * 获取书籍平均评分
     * @param bookId 书籍ID
     * @return 平均评分
     */
    Double getBookRating(Integer bookId);
    
    /**
     * 获取用户对书籍的评分
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 评分对象
     */
    Rating getUserRating(Integer userId, Integer bookId);
    
    /**
     * 获取书籍评分数量
     * @param bookId 书籍ID
     * @return 评分数量
     */
    int getRatingCount(Integer bookId);
    
    /**
     * 获取所有评分
     * @return 评分列表
     */
    List<Rating> getAllRatings();
    
    /**
     * 管理员删除评分
     * @param id 评分ID
     * @return 删除结果
     */
    boolean deleteRatingByAdmin(Integer id);
}
