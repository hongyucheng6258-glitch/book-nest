package com.example.book.service.impl;

import com.example.book.common.PageResult;
import com.example.book.entity.Book;
import com.example.book.mapper.BookMapper;
import com.example.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 整合查询：分页+分类+关键字+排序
     */
    @Override
    public PageResult<Book> getBookByQuery(Integer page, Integer limit, Integer categoryId, String keyword, String sortBy) {
        // 1. 处理默认参数（防止前端未传值）
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;
        if (categoryId == null) categoryId = 0;
        if (sortBy == null) sortBy = "default";

        // 2. 计算分页偏移量（MySQL分页：LIMIT offset, limit）
        int offset = (page - 1) * limit;

        // 3. 查当前页数据 + 查总记录数
        List<Book> records = bookMapper.selectByQuery(offset, limit, categoryId, keyword, sortBy);
        long totalRow = bookMapper.selectCountByQuery(categoryId, keyword);

        // 4. 构造分页结果返回
        return new PageResult<>(page, limit, totalRow, records);
    }

    /**
     * 关键字搜索（无分页）
     */
    @Override
    public List<Book> getBookByKeyword(String keyword) {
        // 调用Mapper查询所有匹配数据
        return bookMapper.selectByKeyword(keyword);
    }

    /**
     * 单纯分页查询
     */
    @Override
    public PageResult<Book> getBookPaginate(Integer page, Integer limit) {
        // 1. 处理默认参数
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;

        // 2. 计算偏移量
        int offset = (page - 1) * limit;

        // 3. 查数据 + 查总记录数
        List<Book> records = bookMapper.selectByPaginate(offset, limit);
        long totalRow = bookMapper.selectTotalCount();

        // 4. 构造分页结果
        return new PageResult<>(page, limit, totalRow, records);
    }
    
    /**
     * 根据ID查询书籍详情
     */
    @Override
    public Book getBookById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("书籍ID不能为空");
        }
        return bookMapper.selectById(id);
    }
    
    /**
     * 添加书籍
     */
    @Override
    public int addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("书籍信息不能为空");
        }
        
        // 设置默认库存
        if (book.getStock() == null) {
            book.setStock(0);
        }
        
        // 获取可用的最小书籍ID
        Integer availableId = bookMapper.getAvailableBookId();
        book.setId(availableId);
        
        return bookMapper.insert(book);
    }
    
    /**
     * 修改书籍
     */
    @Override
    public int updateBook(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("书籍ID不能为空");
        }
        return bookMapper.update(book);
    }
    
    /**
     * 删除书籍
     */
    @Override
    public int deleteBook(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("书籍ID不能为空");
        }
        return bookMapper.deleteById(id);
    }
    
    /**
     * 检查书籍库存是否充足
     */
    @Override
    public boolean checkStock(Integer bookId, Integer quantity) {
        if (bookId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("参数异常");
        }
        
        Integer stock = bookMapper.selectStockById(bookId);
        return stock != null && stock >= quantity;
    }
    
    /**
     * 扣减书籍库存
     */
    @Override
    public int decreaseStock(Integer bookId, Integer quantity) {
        if (bookId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("参数异常");
        }
        
        return bookMapper.decreaseStock(bookId, quantity);
    }
    
    /**
     * 更新书籍库存
     */
    @Override
    public int updateStock(Integer bookId, Integer stock) {
        if (bookId == null || stock == null || stock < 0) {
            throw new IllegalArgumentException("参数异常");
        }
        
        return bookMapper.updateStock(bookId, stock);
    }
}