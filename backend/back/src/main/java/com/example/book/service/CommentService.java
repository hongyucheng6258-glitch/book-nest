package com.example.book.service;

import com.example.book.entity.Comment;
import java.util.List;

/**
 * 评论服务接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface CommentService {
    
    /**
     * 添加评论
     * @param comment 评论对象
     * @return 添加结果
     */
    boolean addComment(Comment comment);
    
    /**
     * 获取书籍评论列表
     * @param bookId 书籍ID
     * @return 评论列表
     */
    List<Comment> getCommentsByBookId(Integer bookId);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteComment(Integer id, Integer userId);
    
    /**
     * 获取书籍评论数量
     * @param bookId 书籍ID
     * @return 评论数量
     */
    int getCommentCount(Integer bookId);
    
    /**
     * 获取评论详情
     * @param id 评论ID
     * @return 评论对象
     */
    Comment getCommentById(Integer id);
    
    /**
     * 检查评论是否属于用户
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否属于用户
     */
    boolean checkCommentOwner(Integer commentId, Integer userId);
    
    /**
     * 获取所有评论
     * @return 评论列表
     */
    List<Comment> getAllComments();
    
    /**
     * 管理员删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    boolean deleteCommentByAdmin(Integer id);
    
    /**
     * 获取书籍评论列表，支持排序
     * @param bookId 书籍ID
     * @param sortBy 排序字段（createdAt或likeCount）
     * @param order 排序顺序（asc或desc）
     * @return 评论列表
     */
    List<Comment> getCommentsByBookIdWithSort(Integer bookId, String sortBy, String order);
    
    /**
     * 获取评论的子回复列表
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<Comment> getRepliesByParentId(Integer parentId);
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 点赞结果
     */
    boolean likeComment(Integer commentId, Integer userId);
    
    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 取消点赞结果
     */
    boolean unlikeComment(Integer commentId, Integer userId);
    
    /**
     * 查询用户是否点赞了评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean isCommentLiked(Integer commentId, Integer userId);
    
    /**
     * 更新评论的点赞数量
     * @param commentId 评论ID
     * @return 更新结果
     */
    boolean updateCommentLikeCount(Integer commentId);
}
