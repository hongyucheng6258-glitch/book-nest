package com.example.book.mapper;

import com.example.book.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 收藏表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface FavoriteMapper {
    // 根据用户ID查询收藏列表
    List<Favorite> selectByUserId(Integer userId);

    // 根据用户ID和书籍ID查询收藏
    Favorite selectByUserIdAndBookId(Integer userId, Integer bookId);

    // 插入收藏
    int insert(Favorite favorite);

    // 根据用户ID和书籍ID删除收藏
    int deleteByUserIdAndBookId(Integer userId, Integer bookId);
}