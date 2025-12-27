package com.example.book.mapper;

import com.example.book.entity.Comment;
import com.example.book.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface CommentMapper {
    
    /**
     * 添加评论
     * @param comment 评论对象
     * @return 影响行数
     */
    int insert(Comment comment);
    
    /**
     * 根据书籍ID获取评论列表
     * @param bookId 书籍ID
     * @return 评论列表
     */
    List<Comment> selectByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 根据评论ID删除评论
     * @param id 评论ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据书籍ID获取评论数量
     * @param bookId 书籍ID
     * @return 评论数量
     */
    int selectCountByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 根据评论ID获取评论
     * @param id 评论ID
     * @return 评论对象
     */
    Comment selectById(@Param("id") Integer id);
    
    /**
     * 根据用户ID和书籍ID获取评论
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 评论对象
     */
    Comment selectByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);
    
    /**
     * 获取所有评论
     * @return 评论列表
     */
    List<Comment> selectAll();
    
    /**
     * 插入评论点赞记录
     * @param commentLike 评论点赞对象
     * @return 影响行数
     */
    int insertCommentLike(CommentLike commentLike);
    
    /**
     * 删除评论点赞记录
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCommentLike(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
    
    /**
     * 查询用户是否点赞了某条评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    CommentLike selectCommentLike(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
    
    /**
     * 查询评论的点赞数量
     * @param commentId 评论ID
     * @return 点赞数量
     */
    int selectLikeCountByCommentId(@Param("commentId") Integer commentId);
    
    /**
     * 查询评论的子评论列表
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<Comment> selectRepliesByParentId(@Param("parentId") Integer parentId);
    
    /**
     * 根据书籍ID获取评论列表，支持排序
     * @param bookId 书籍ID
     * @param sortBy 排序字段（createdAt或likeCount）
     * @param order 排序顺序（asc或desc）
     * @return 评论列表
     */
    List<Comment> selectByBookIdWithSort(@Param("bookId") Integer bookId, @Param("sortBy") String sortBy, @Param("order") String order);
    
    /**
     * 更新评论的点赞数量
     * @param commentId 评论ID
     * @param likeCount 点赞数量
     * @return 影响行数
     */
    int updateLikeCount(@Param("commentId") Integer commentId, @Param("likeCount") Integer likeCount);
}
