package com.example.book.mapper;

import com.example.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BookMapper {
    // 1. 整合查询：查当前页数据
    List<Book> selectByQuery(Integer offset, Integer limit, Integer categoryId, String keyword, String sortBy);
    // 1. 整合查询：查总记录数
    long selectCountByQuery(Integer categoryId, String keyword);

    // 2. 关键字搜索：查所有匹配数据
    List<Book> selectByKeyword(String keyword);

    // 3. 单纯分页：查当前页数据
    List<Book> selectByPaginate(Integer offset, Integer limit);
    // 3. 单纯分页：查总记录数
    long selectTotalCount();
    
    // 4. 根据ID查询书籍详情
    Book selectById(Integer id);
    
    // 5. 添加书籍
    int insert(Book book);
    
    // 6. 修改书籍
    int update(Book book);
    
    // 7. 删除书籍
    int deleteById(Integer id);
    
    // 8. 获取可用的最小书籍ID（从1开始，重用已删除的ID）
    Integer getAvailableBookId();
    
    // 9. 检查库存是否充足
    Integer selectStockById(Integer id);
    
    // 10. 扣减库存
    int decreaseStock(Integer id, Integer quantity);
    
    // 11. 更新库存
    int updateStock(Integer id, Integer stock);
}