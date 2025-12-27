package com.example.book.mapper;

import com.example.book.entity.Rating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评分表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface RatingMapper {
    
    /**
     * 添加评分
     * @param rating 评分对象
     * @return 影响行数
     */
    int insert(Rating rating);
    
    /**
     * 更新评分
     * @param rating 评分对象
     * @return 影响行数
     */
    int update(Rating rating);
    
    /**
     * 根据书籍ID获取平均评分
     * @param bookId 书籍ID
     * @return 平均评分
     */
    Double selectAverageByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 根据用户ID和书籍ID获取评分
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 评分对象
     */
    Rating selectByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);
    
    /**
     * 根据书籍ID获取评分数量
     * @param bookId 书籍ID
     * @return 评分数量
     */
    int selectCountByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 根据评分ID获取评分
     * @param id 评分ID
     * @return 评分对象
     */
    Rating selectById(@Param("id") Integer id);
    
    /**
     * 获取所有评分
     * @return 评分列表
     */
    List<Rating> selectAll();
    
    /**
     * 根据评分ID删除评分
     * @param id 评分ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
}
