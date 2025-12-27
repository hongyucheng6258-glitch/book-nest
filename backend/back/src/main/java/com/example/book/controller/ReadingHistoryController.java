package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.ReadingHistory;
import com.example.book.service.ReadingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅读历史记录 Controller
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/history")
public class ReadingHistoryController {
    
    @Autowired
    private ReadingHistoryService readingHistoryService;
    
    /**
     * 添加或更新阅读记录
     * @param readingHistory 阅读记录对象
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<?> addOrUpdateHistory(@RequestBody ReadingHistory readingHistory) {
        boolean result = readingHistoryService.addOrUpdateHistory(readingHistory);
        if (result) {
            return new Result<>(true, "添加或更新阅读记录成功");
        } else {
            return new Result<>(false, "添加或更新阅读记录失败");
        }
    }
    
    /**
     * 根据用户ID获取阅读历史记录
     * @param userId 用户ID
     * @return 阅读历史记录列表
     */
    @GetMapping("/user")
    public Result<List<ReadingHistory>> getReadingHistory(@RequestParam Integer userId) {
        System.out.println("获取阅读历史记录，用户ID: " + userId);
        List<ReadingHistory> readingHistoryList = readingHistoryService.getReadingHistory(userId);
        System.out.println("阅读历史记录数量: " + (readingHistoryList != null ? readingHistoryList.size() : 0));
        if (readingHistoryList != null && !readingHistoryList.isEmpty()) {
            System.out.println("第一条阅读记录ID: " + readingHistoryList.get(0).getId());
            System.out.println("第一条阅读记录用户ID: " + readingHistoryList.get(0).getUserId());
            System.out.println("第一条阅读记录书籍ID: " + readingHistoryList.get(0).getBookId());
        }
        return new Result<>(readingHistoryList);
    }
    
    /**
     * 根据阅读记录ID删除阅读记录
     * @param id 阅读记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteHistory(@PathVariable Integer id) {
        boolean result = readingHistoryService.deleteHistory(id);
        if (result) {
            return new Result<>(true, "删除阅读记录成功");
        } else {
            return new Result<>(false, "删除阅读记录失败");
        }
    }
    
    /**
     * 清空用户阅读历史记录
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Result<?> clearHistory(@RequestParam Integer userId) {
        boolean result = readingHistoryService.clearHistory(userId);
        if (result) {
            return new Result<>(true, "清空阅读历史记录成功");
        } else {
            return new Result<>(false, "清空阅读历史记录失败");
        }
    }
    
    /**
     * 根据用户ID获取阅读历史数量
     * @param userId 用户ID
     * @return 阅读历史数量
     */
    @GetMapping("/count")
    public Result<?> getHistoryCount(@RequestParam Integer userId) {
        int count = readingHistoryService.getHistoryCount(userId);
        return new Result<>(count);
    }
}