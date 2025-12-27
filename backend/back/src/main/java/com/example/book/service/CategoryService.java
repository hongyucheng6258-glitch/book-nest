package com.example.book.service;

import com.example.book.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    // 获取所有分类
    List<Category> getAllCategory();
    
    // 根据ID获取分类
    Category getCategoryById(Integer id);
    
    // 添加分类
    int addCategory(Category category);
    
    // 更新分类
    int updateCategory(Category category);
    
    // 删除分类
    int deleteCategory(Integer id);
}