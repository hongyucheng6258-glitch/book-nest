package com.example.book.service.impl;

import com.example.book.entity.Rating;
import com.example.book.mapper.RatingMapper;
import com.example.book.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评分服务实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class RatingServiceImpl implements RatingService {
    
    @Autowired
    private RatingMapper ratingMapper;
    
    @Override
    public boolean addOrUpdateRating(Rating rating) {
        if (rating == null || rating.getUserId() == null || rating.getBookId() == null || rating.getRating() == null) {
            return false;
        }
        
        // 验证评分范围（1-5）
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            return false;
        }
        
        // 检查用户是否已经对该书籍评分
        Rating existingRating = ratingMapper.selectByUserIdAndBookId(rating.getUserId(), rating.getBookId());
        
        if (existingRating != null) {
            // 更新已有评分
            existingRating.setRating(rating.getRating());
            existingRating.setUpdatedAt(LocalDateTime.now());
            int result = ratingMapper.update(existingRating);
            return result > 0;
        } else {
            // 添加新评分
            rating.setCreatedAt(LocalDateTime.now());
            rating.setUpdatedAt(LocalDateTime.now());
            int result = ratingMapper.insert(rating);
            return result > 0;
        }
    }
    
    @Override
    public Double getBookRating(Integer bookId) {
        if (bookId == null) {
            return 0.0;
        }
        
        Double averageRating = ratingMapper.selectAverageByBookId(bookId);
        return averageRating != null ? averageRating : 0.0;
    }
    
    @Override
    public Rating getUserRating(Integer userId, Integer bookId) {
        if (userId == null || bookId == null) {
            return null;
        }
        
        return ratingMapper.selectByUserIdAndBookId(userId, bookId);
    }
    
    @Override
    public int getRatingCount(Integer bookId) {
        if (bookId == null) {
            return 0;
        }
        
        return ratingMapper.selectCountByBookId(bookId);
    }
    
    @Override
    public List<Rating> getAllRatings() {
        return ratingMapper.selectAll();
    }
    
    @Override
    public boolean deleteRatingByAdmin(Integer id) {
        if (id == null) {
            return false;
        }
        
        int result = ratingMapper.deleteById(id);
        return result > 0;
    }
}
