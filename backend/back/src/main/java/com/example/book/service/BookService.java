package com.example.book.service;

import com.example.book.common.PageResult;
import com.example.book.entity.Book;
import java.util.List;

public interface BookService {
    /**
     * 1. 整合查询（分页+分类筛选+关键字搜索）→ 对应第二题、第五题
     * @param page 页码
     * @param limit 每页条数
     * @param categoryId 分类ID（0表示全部）
     * @param keyword 搜索关键字（匹配书名、出版社、作者）
     * @param sortBy 排序方式（default、price_asc、price_desc）
     * @return 分页结果
     */
    PageResult<Book> getBookByQuery(Integer page, Integer limit, Integer categoryId, String keyword, String sortBy);

    /**
     * 2. 关键字搜索（无分页）→ 对应第三题
     * @param keyword 搜索关键字
     * @return 匹配的书籍列表
     */
    List<Book> getBookByKeyword(String keyword);

    /**
     * 3. 单纯分页查询（无筛选）→ 对应第四题
     * @param page 页码
     * @param limit 每页条数
     * @return 分页结果
     */
    PageResult<Book> getBookPaginate(Integer page, Integer limit);
    
    /**
     * 4. 根据ID查询书籍详情
     * @param id 书籍ID
     * @return 书籍详情
     */
    Book getBookById(Integer id);
    
    /**
     * 5. 添加书籍
     * @param book 书籍信息
     * @return 影响行数
     */
    int addBook(Book book);
    
    /**
     * 6. 修改书籍
     * @param book 书籍信息
     * @return 影响行数
     */
    int updateBook(Book book);
    
    /**
     * 7. 删除书籍
     * @param id 书籍ID
     * @return 影响行数
     */
    int deleteBook(Integer id);
    
    /**
     * 8. 检查书籍库存是否充足
     * @param bookId 书籍ID
     * @param quantity 需要的数量
     * @return true表示库存充足，false表示库存不足
     */
    boolean checkStock(Integer bookId, Integer quantity);
    
    /**
     * 9. 扣减书籍库存
     * @param bookId 书籍ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    int decreaseStock(Integer bookId, Integer quantity);
    
    /**
     * 10. 更新书籍库存
     * @param bookId 书籍ID
     * @param stock 新的库存数量
     * @return 影响行数
     */
    int updateStock(Integer bookId, Integer stock);
}