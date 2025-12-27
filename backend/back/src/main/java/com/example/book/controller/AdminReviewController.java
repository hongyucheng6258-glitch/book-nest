package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.Comment;
import com.example.book.entity.Rating;
import com.example.book.service.CommentService;
import com.example.book.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价与评分管理控制器
 * 用于后台管理系统的评价和评分管理功能
 */
@RestController
@RequestMapping("/api/admin/review")
public class AdminReviewController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    /**
     * 获取所有评论，支持分页和搜索
     * @param page 当前页码
     * @param size 每页大小
     * @param searchKey 搜索关键词
     * @return 评论列表
     */
    @GetMapping("/comments")
    public Result<List<Comment>> getAllComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String searchKey) {
        List<Comment> comments = commentService.getAllComments();
        return new Result<>(comments);
    }

    /**
     * 管理员删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/comment/{id}")
    public Result<?> deleteComment(@PathVariable Integer id) {
        boolean result = commentService.deleteCommentByAdmin(id);
        if (result) {
            return new Result<>(true, "删除评论成功");
        } else {
            return new Result<>(false, "删除评论失败");
        }
    }

    /**
     * 获取所有评分，支持分页和搜索
     * @param page 当前页码
     * @param size 每页大小
     * @param searchKey 搜索关键词
     * @return 评分列表
     */
    @GetMapping("/ratings")
    public Result<List<Rating>> getAllRatings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String searchKey) {
        List<Rating> ratings = ratingService.getAllRatings();
        return new Result<>(ratings);
    }

    /**
     * 管理员删除评分
     * @param id 评分ID
     * @return 删除结果
     */
    @DeleteMapping("/rating/{id}")
    public Result<?> deleteRating(@PathVariable Integer id) {
        boolean result = ratingService.deleteRatingByAdmin(id);
        if (result) {
            return new Result<>(true, "删除评分成功");
        } else {
            return new Result<>(false, "删除评分失败");
        }
    }
}
