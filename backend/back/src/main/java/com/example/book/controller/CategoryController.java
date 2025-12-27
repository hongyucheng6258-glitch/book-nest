package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.Category;
import com.example.book.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 标记为REST接口控制器
@RequestMapping("/api/category") // 接口基础路径（与前端API配置对应）
@CrossOrigin // 允许跨域请求（前端和后端不同端口时必需）
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    // 获取所有分类：GET /api/category/all
    @GetMapping("/all")
    public Result<List<Category>> getAllCategory() {
        try {
            // 使用BaseMapper提供的selectAll()方法查询所有数据
            List<Category> categories = categoryMapper.selectAll();
            return new Result<>(true, categories, "获取分类列表成功");
        } catch (Exception e) {
            throw new BusinessException("获取分类列表失败: " + e.getMessage());
        }
    }
    
    // 根据ID获取分类：GET /api/category/{id}
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Integer id) {
        try {
            // 使用BaseMapper提供的selectById()方法根据ID查询
            Category category = categoryMapper.selectById(id);
            return new Result<>(true, category, "获取分类成功");
        } catch (Exception e) {
            throw new BusinessException("获取分类失败: " + e.getMessage());
        }
    }
    
    // 添加分类：POST /api/category/admin/add
    @PostMapping("/admin/add")
    public Result<Integer> addCategory(@RequestBody Category category) {
        try {
            // 使用BaseMapper提供的insert()方法添加数据
            int result = categoryMapper.insert(category);
            return new Result<>(true, result, "添加分类成功");
        } catch (Exception e) {
            throw new BusinessException("添加分类失败: " + e.getMessage());
        }
    }
    
    // 修改分类：PUT /api/category/admin/update
    @PutMapping("/admin/update")
    public Result<Integer> updateCategory(@RequestBody Category category) {
        try {
            // 使用BaseMapper提供的update()方法更新数据
            int result = categoryMapper.update(category);
            return new Result<>(true, result, "更新分类成功");
        } catch (Exception e) {
            throw new BusinessException("更新分类失败: " + e.getMessage());
        }
    }
    
    // 修改分类ID：PUT /api/category/admin/updateId/{oldId}
    @PutMapping("/admin/updateId/{oldId}")
    public Result<Integer> updateCategoryId(@PathVariable Integer oldId, @RequestBody Category category) {
        try {
            // 先删除旧ID的分类
            int deleteResult = categoryMapper.deleteById(oldId);
            if (deleteResult > 0) {
                // 再插入新ID的分类
                int insertResult = categoryMapper.insert(category);
                return new Result<>(true, insertResult, "更新分类ID成功");
            } else {
                return new Result<>(false, 0, "更新分类ID失败：未找到旧ID的分类");
            }
        } catch (Exception e) {
            throw new BusinessException("更新分类ID失败: " + e.getMessage());
        }
    }
    
    // 删除分类：DELETE /api/category/admin/delete/{id}
    @DeleteMapping("/admin/delete/{id}")
    public Result<Integer> deleteCategory(@PathVariable Integer id) {
        try {
            // 使用BaseMapper提供的deleteById()方法删除数据
            int result = categoryMapper.deleteById(id);
            return new Result<>(true, result, "删除分类成功");
        } catch (Exception e) {
            throw new BusinessException("删除分类失败: " + e.getMessage());
        }
    }
}