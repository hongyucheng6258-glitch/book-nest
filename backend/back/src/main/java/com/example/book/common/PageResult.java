package com.example.book.common;

import java.util.List;

/**
 * 分页结果统一返回类
 * 适配前端预期的 {records: [...数据列表...], totalRow: 总条数, total: 总条数} 结构
 * @param <T> 数据类型（如 Book 实体）
 */
public class PageResult<T> {
    private Long totalRow;       // 总记录数（对应前端 totalRow）
    private Long total;          // 总记录数（对应前端 total）
    private List<T> records;     // 当前页数据列表（对应前端 records）
    private Integer pageNum;     // 当前页码（可选，前端若需页码信息可保留）
    private Integer pageSize;    // 每页条数（可选，前端若需页大小信息可保留）

    // 核心构造方法（仅传递“总条数”和“数据列表”，满足基础需求）
    public PageResult(Long totalRow, List<T> records) {
        this.totalRow = totalRow;
        this.total = totalRow;
        this.records = records;
    }

    // 扩展构造方法（若前端需要“页码/页大小”信息，可使用此构造）
    public PageResult(Integer pageNum, Integer pageSize, Long totalRow, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalRow = totalRow;
        this.total = totalRow;
        this.records = records;
    }

    // Getter（前端通过 JSON 解析时，字段名需与 Getter 方法名匹配）
    public Long getTotalRow() { return totalRow; }
    public Long getTotal() { return total; }
    public List<T> getRecords() { return records; }
    public Integer getPageNum() { return pageNum; }
    public Integer getPageSize() { return pageSize; }

    // Setter（按需使用，若需修改结果可调用）
    public void setTotalRow(Long totalRow) { 
        this.totalRow = totalRow;
        this.total = totalRow;
    }
    public void setTotal(Long total) { 
        this.total = total;
        this.totalRow = total;
    }
    public void setRecords(List<T> records) { this.records = records; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}