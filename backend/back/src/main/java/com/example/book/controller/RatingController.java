package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.Rating;
import com.example.book.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评分控制器
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rating")
public class RatingController {
    
    @Autowired
    private RatingService ratingService;
    
    /**
     * 添加或更新评分
     * @param rating 评分对象
     * @return 响应结果
     */
    @PostMapping("/add")
    public Result<?> addOrUpdateRating(@RequestBody Rating rating) {
        boolean result = ratingService.addOrUpdateRating(rating);
        if (result) {
            return new Result<>(true, "评分成功");
        } else {
            return new Result<>(false, "评分失败");
        }
    }
    
    /**
     * 根据书籍ID获取平均评分
     * @param bookId 书籍ID
     * @return 响应结果
     */
    @GetMapping("/{bookId}")
    public Result<?> getRatingByBookId(@PathVariable Integer bookId) {
        Double rating = ratingService.getBookRating(bookId);
        return new Result<>(rating);
    }
    
    /**
     * 根据用户ID和书籍ID获取评分
     * @param bookId 书籍ID
     * @param userId 用户ID
     * @return 响应结果
     */
    @GetMapping("/user/{bookId}")
    public Result<?> getUserRatingByBookId(@PathVariable Integer bookId, @RequestParam Integer userId) {
        Rating rating = ratingService.getUserRating(userId, bookId);
        return new Result<>(rating);
    }
    
    /**
     * 根据书籍ID获取评分数量
     * @param bookId 书籍ID
     * @return 响应结果
     */
    @GetMapping("/count/{bookId}")
    public Result<?> getRatingCountByBookId(@PathVariable Integer bookId) {
        int count = ratingService.getRatingCount(bookId);
        return new Result<>(count);
    }
}
