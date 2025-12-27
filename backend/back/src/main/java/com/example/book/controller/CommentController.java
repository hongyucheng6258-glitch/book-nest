package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.Comment;
import com.example.book.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 添加评论
     * @param comment 评论对象
     * @return 响应结果
     */
    @PostMapping("/add")
    public Result<?> addComment(@RequestBody Comment comment) {
        boolean result = commentService.addComment(comment);
        if (result) {
            return new Result<>(true, "添加评论成功");
        } else {
            return new Result<>(false, "添加评论失败");
        }
    }
    
    /**
     * 根据书籍ID获取评论列表
     * @param bookId 书籍ID
     * @return 响应结果
     */
    @GetMapping("/{bookId}")
    public Result<?> getCommentsByBookId(@PathVariable Integer bookId) {
        List<Comment> comments = commentService.getCommentsByBookId(bookId);
        return new Result<>(comments);
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteComment(@PathVariable Integer id, @RequestParam Integer userId) {
        boolean result = commentService.deleteComment(id, userId);
        if (result) {
            return new Result<>(true, "删除评论成功");
        } else {
            return new Result<>(false, "删除评论失败");
        }
    }
    
    /**
     * 根据书籍ID获取评论数量
     * @param bookId 书籍ID
     * @return 响应结果
     */
    @GetMapping("/count/{bookId}")
    public Result<?> getCommentCount(@PathVariable Integer bookId) {
        int count = commentService.getCommentCount(bookId);
        return new Result<>(count);
    }
    
    /**
     * 根据书籍ID获取评论列表，支持排序
     * @param bookId 书籍ID
     * @param sortBy 排序字段（createdAt或likeCount）
     * @param order 排序顺序（asc或desc）
     * @return 响应结果
     */
    @GetMapping("/sort/{bookId}")
    public Result<?> getCommentsByBookIdWithSort(
            @PathVariable Integer bookId,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        List<Comment> comments = commentService.getCommentsByBookIdWithSort(bookId, sortBy, order);
        return new Result<>(comments);
    }
    
    /**
     * 获取评论的子回复列表
     * @param parentId 父评论ID
     * @return 响应结果
     */
    @GetMapping("/replies/{parentId}")
    public Result<?> getRepliesByParentId(@PathVariable Integer parentId) {
        List<Comment> replies = commentService.getRepliesByParentId(parentId);
        return new Result<>(replies);
    }
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 响应结果
     */
    @PostMapping("/like")
    public Result<?> likeComment(@RequestParam Integer commentId, @RequestParam Integer userId) {
        boolean result = commentService.likeComment(commentId, userId);
        if (result) {
            return new Result<>(true, "点赞成功");
        } else {
            return new Result<>(false, "点赞失败");
        }
    }
    
    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 响应结果
     */
    @PostMapping("/unlike")
    public Result<?> unlikeComment(@RequestParam Integer commentId, @RequestParam Integer userId) {
        boolean result = commentService.unlikeComment(commentId, userId);
        if (result) {
            return new Result<>(true, "取消点赞成功");
        } else {
            return new Result<>(false, "取消点赞失败");
        }
    }
    
    /**
     * 查询用户是否点赞了评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 响应结果
     */
    @GetMapping("/isLiked")
    public Result<?> isCommentLiked(@RequestParam Integer commentId, @RequestParam Integer userId) {
        boolean isLiked = commentService.isCommentLiked(commentId, userId);
        return new Result<>(isLiked);
    }
}
