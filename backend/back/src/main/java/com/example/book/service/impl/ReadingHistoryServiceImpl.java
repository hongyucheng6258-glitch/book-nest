package com.example.book.service.impl;

import com.example.book.entity.ReadingHistory;
import com.example.book.mapper.ReadingHistoryMapper;
import com.example.book.service.ReadingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 阅读历史记录 Service 实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class ReadingHistoryServiceImpl implements ReadingHistoryService {
    
    @Autowired
    private ReadingHistoryMapper readingHistoryMapper;
    
    @Override
    public boolean addOrUpdateHistory(ReadingHistory readingHistory) {
        if (readingHistory == null || readingHistory.getUserId() == null || readingHistory.getBookId() == null) {
            return false;
        }
        
        int result = readingHistoryMapper.addOrUpdateHistory(readingHistory);
        return result > 0;
    }
    
    @Override
    public List<ReadingHistory> getReadingHistory(Integer userId) {
        if (userId == null) {
            return null;
        }
        
        return readingHistoryMapper.getReadingHistoryByUserId(userId);
    }
    
    @Override
    public boolean deleteHistory(Integer id) {
        if (id == null) {
            return false;
        }
        
        int result = readingHistoryMapper.deleteHistoryById(id);
        return result > 0;
    }
    
    @Override
    public boolean clearHistory(Integer userId) {
        if (userId == null) {
            return false;
        }
        
        int result = readingHistoryMapper.deleteAllHistoryByUserId(userId);
        return result > 0;
    }
    
    @Override
    public int getHistoryCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        
        return readingHistoryMapper.getHistoryCountByUserId(userId);
    }
}