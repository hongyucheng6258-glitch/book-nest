package com.example.book.mapper;

import com.example.book.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper // 标记为MyBatis Mapper接口
public interface CategoryMapper {
    // 查询所有分类
    List<Category> selectAll();
    
    // 根据ID查询分类
    Category selectById(Integer id);
    
    // 添加分类
    int insert(Category category);
    
    // 修改分类
    int update(Category category);
    
    // 删除分类
    int deleteById(Integer id);
}