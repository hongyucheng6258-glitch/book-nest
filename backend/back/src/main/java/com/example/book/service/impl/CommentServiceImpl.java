package com.example.book.service.impl;

import com.example.book.entity.Comment;
import com.example.book.entity.CommentLike;
import com.example.book.mapper.CommentMapper;
import com.example.book.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public boolean addComment(Comment comment) {
        if (comment == null || comment.getUserId() == null || comment.getBookId() == null || comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return false;
        }
        
        // 设置创建时间和更新时间
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // 调用Mapper添加评论
        int result = commentMapper.insert(comment);
        return result > 0;
    }
    
    @Override
    public List<Comment> getCommentsByBookId(Integer bookId) {
        if (bookId == null) {
            return null;
        }
        return commentMapper.selectByBookId(bookId);
    }
    
    @Override
    public boolean deleteComment(Integer id, Integer userId) {
        if (id == null || userId == null) {
            return false;
        }
        
        // 检查评论是否存在
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return false;
        }
        
        // 检查评论是否属于当前用户
        if (!comment.getUserId().equals(userId)) {
            return false;
        }
        
        // 调用Mapper删除评论
        int result = commentMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public int getCommentCount(Integer bookId) {
        if (bookId == null) {
            return 0;
        }
        return commentMapper.selectCountByBookId(bookId);
    }
    
    @Override
    public Comment getCommentById(Integer id) {
        if (id == null) {
            return null;
        }
        return commentMapper.selectById(id);
    }
    
    @Override
    public boolean checkCommentOwner(Integer commentId, Integer userId) {
        if (commentId == null || userId == null) {
            return false;
        }
        
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return false;
        }
        
        return comment.getUserId().equals(userId);
    }
    
    @Override
    public List<Comment> getAllComments() {
        return commentMapper.selectAll();
    }
    
    @Override
    public boolean deleteCommentByAdmin(Integer id) {
        if (id == null) {
            return false;
        }
        
        int result = commentMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public List<Comment> getCommentsByBookIdWithSort(Integer bookId, String sortBy, String order) {
        if (bookId == null) {
            return null;
        }
        
        // 验证排序字段和顺序
        if (sortBy == null || (!"createdAt".equals(sortBy) && !"likeCount".equals(sortBy))) {
            sortBy = "createdAt";
        }
        
        if (order == null || (!"asc".equals(order) && !"desc".equals(order))) {
            order = "desc";
        }
        
        // 将驼峰命名转换为下划线命名，适配数据库列名
        String dbSortBy = sortBy;
        if ("createdAt".equals(sortBy)) {
            dbSortBy = "created_at";
        } else if ("likeCount".equals(sortBy)) {
            dbSortBy = "like_count";
        }
        
        return commentMapper.selectByBookIdWithSort(bookId, dbSortBy, order);
    }
    
    @Override
    public List<Comment> getRepliesByParentId(Integer parentId) {
        if (parentId == null) {
            return null;
        }
        
        return commentMapper.selectRepliesByParentId(parentId);
    }
    
    @Override
    public boolean likeComment(Integer commentId, Integer userId) {
        if (commentId == null || userId == null) {
            return false;
        }
        
        // 检查是否已经点赞
        if (isCommentLiked(commentId, userId)) {
            return true; // 已经点赞，直接返回成功
        }
        
        // 创建点赞记录
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLike.setCreatedAt(LocalDateTime.now());
        
        // 添加点赞记录
        int result = commentMapper.insertCommentLike(commentLike);
        if (result > 0) {
            // 更新评论的点赞数量
            updateCommentLikeCount(commentId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean unlikeComment(Integer commentId, Integer userId) {
        if (commentId == null || userId == null) {
            return false;
        }
        
        // 检查是否已经点赞
        if (!isCommentLiked(commentId, userId)) {
            return true; // 没有点赞，直接返回成功
        }
        
        // 删除点赞记录
        int result = commentMapper.deleteCommentLike(commentId, userId);
        if (result > 0) {
            // 更新评论的点赞数量
            updateCommentLikeCount(commentId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isCommentLiked(Integer commentId, Integer userId) {
        if (commentId == null || userId == null) {
            return false;
        }
        
        CommentLike commentLike = commentMapper.selectCommentLike(commentId, userId);
        return commentLike != null;
    }
    
    @Override
    public boolean updateCommentLikeCount(Integer commentId) {
        if (commentId == null) {
            return false;
        }
        
        // 查询点赞数量
        int likeCount = commentMapper.selectLikeCountByCommentId(commentId);
        
        // 更新评论的点赞数量
        int result = commentMapper.updateLikeCount(commentId, likeCount);
        return result > 0;
    }
}
