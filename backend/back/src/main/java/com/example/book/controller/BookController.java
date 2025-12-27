package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.PageResult;
import com.example.book.common.Result;
import com.example.book.entity.Book;
import com.example.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book") // 前端调用基础路径：/api/book/xxx（与前端一致）
@CrossOrigin // 允许跨域
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 1. 整合查询（分页+分类+关键字+排序）→ 前端调用
     * 关键修改：添加sortBy参数用于排序
     */
    @GetMapping("/query")
    public Result<PageResult<Book>> getBookByQuery(
            Integer currentPage, // 对应前端currentPage
            Integer pageSize,    // 对应前端pageSize
            Integer categoryId,
            String keyword,
            String sortBy        // 对应前端sortBy
    ) {
        try {
            // 补充默认值：防止前端未传参导致的空指针
            if (currentPage == null) currentPage = 1;
            if (pageSize == null) pageSize = 10;
            PageResult<Book> result = bookService.getBookByQuery(currentPage, pageSize, categoryId, keyword, sortBy);
            return new Result<>(true, result, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 2. 关键字搜索（无分页）→ 前端第三题调用
     */
    @GetMapping("/byKeyword")
    public Result<List<Book>> getBookByKeyword(String keyword) {
        try {
            List<Book> books = bookService.getBookByKeyword(keyword);
            return new Result<>(true, books, "搜索成功");
        } catch (Exception e) {
            throw new BusinessException("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 3. 单纯分页查询 → 前端第四题调用
     * 同样修改参数名与前端对齐
     */
    @GetMapping("/paginate")
    public Result<PageResult<Book>> getBookPaginate(
            Integer currentPage, // 对应前端currentPage
            Integer pageSize     // 对应前端pageSize
    ) {
        try {
            if (currentPage == null) currentPage = 1;
            if (pageSize == null) pageSize = 10;
            PageResult<Book> result = bookService.getBookPaginate(currentPage, pageSize);
            return new Result<>(true, result, "分页查询成功");
        } catch (Exception e) {
            throw new BusinessException("分页查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 4. 根据ID查询书籍详情 → 前端书籍详情页调用
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Integer id) {
        try {
            Book book = bookService.getBookById(id);
            return new Result<>(true, book, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("获取书籍详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 5. 添加书籍 → 管理员功能
     */
    @PostMapping("/admin/add")
    public Result<Integer> addBook(@RequestBody Book book) {
        try {
            int result = bookService.addBook(book);
            return new Result<>(true, result, "添加书籍成功");
        } catch (Exception e) {
            throw new BusinessException("添加书籍失败: " + e.getMessage());
        }
    }
    
    /**
     * 6. 修改书籍 → 管理员功能
     */
    @PutMapping("/admin/update")
    public Result<Integer> updateBook(@RequestBody Book book) {
        try {
            int result = bookService.updateBook(book);
            return new Result<>(true, result, "修改书籍成功");
        } catch (Exception e) {
            throw new BusinessException("修改书籍失败: " + e.getMessage());
        }
    }
    
    /**
     * 7. 删除书籍 → 管理员功能
     */
    @DeleteMapping("/admin/delete/{id}")
    public Result<Integer> deleteBook(@PathVariable Integer id) {
        try {
            int result = bookService.deleteBook(id);
            return new Result<>(true, result, "删除书籍成功");
        } catch (Exception e) {
            throw new BusinessException("删除书籍失败: " + e.getMessage());
        }
    }
    
    /**
     * 8. 更新书籍库存 → 管理员功能
     */
    @PutMapping("/admin/updateStock")
    public Result<Integer> updateStock(@RequestParam Integer id, @RequestParam Integer stock) {
        try {
            int result = bookService.updateStock(id, stock);
            return new Result<>(true, result, "更新库存成功");
        } catch (Exception e) {
            throw new BusinessException("更新库存失败: " + e.getMessage());
        }
    }
}