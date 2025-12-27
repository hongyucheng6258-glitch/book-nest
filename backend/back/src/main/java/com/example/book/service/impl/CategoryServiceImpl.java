package com.example.book.service.impl;

import com.example.book.entity.Category;
import com.example.book.mapper.CategoryMapper;
import com.example.book.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategory() {
        // 使用 MyBatis-Flex 的查询方法，查询所有数据
        return categoryMapper.selectAll();
    }
    
    @Override
    public Category getCategoryById(Integer id) {
        // 使用 MyBatis-Flex 的查询方法，根据ID查询
        return categoryMapper.selectById(id);
    }
    
    @Override
    public int addCategory(Category category) {
        // 使用 MyBatis-Flex 的插入方法
        return categoryMapper.insert(category);
    }
    
    @Override
    public int updateCategory(Category category) {
        // 使用 MyBatis-Flex 的更新方法
        return categoryMapper.update(category);
    }
    
    @Override
    public int deleteCategory(Integer id) {
        // 使用 MyBatis-Flex 的删除方法
        return categoryMapper.deleteById(id);
    }
}