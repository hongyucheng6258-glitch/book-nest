package com.example.book.service.impl;

import com.example.book.entity.ChatMessage;
import com.example.book.mapper.ChatMessageMapper;
import com.example.book.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息服务实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Override
    public boolean sendMessage(ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getFromUserId() == null || chatMessage.getToUserId() == null || chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
            return false;
        }
        
        // 日志记录：插入前的消息内容
        System.out.println("插入前的消息内容: " + chatMessage.getContent());
        
        // 设置创建时间和已读状态
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setIsRead(0); // 0-未读
        
        // 调用Mapper发送消息
        int result = chatMessageMapper.insert(chatMessage);
        
        // 日志记录：插入结果
        System.out.println("插入结果: " + result);
        
        return result > 0;
    }
    
    @Override
    public boolean deleteMessage(Integer messageId) {
        if (messageId == null) {
            return false;
        }
        
        // 调用Mapper删除消息
        int result = chatMessageMapper.deleteById(messageId);
        return result > 0;
    }
    
    @Override
    public List<ChatMessage> getChatHistory(Integer userId1, Integer userId2, Integer limit) {
        if (userId1 == null || userId2 == null) {
            return null;
        }
        
        // 设置默认消息数量限制
        if (limit == null || limit <= 0) {
            limit = 50;
        }
        
        // 调用Mapper查询聊天记录
        return chatMessageMapper.selectByUserIds(userId1, userId2, limit);
    }
    
    @Override
    public int getUnreadCount(Integer toUserId) {
        if (toUserId == null) {
            return 0;
        }
        
        // 调用Mapper查询未读消息数量
        return chatMessageMapper.selectUnreadCount(toUserId);
    }
    
    @Override
    public boolean markAsRead(Integer fromUserId, Integer toUserId) {
        if (fromUserId == null || toUserId == null) {
            return false;
        }
        
        // 调用Mapper更新消息为已读
        int result = chatMessageMapper.updateReadStatus(fromUserId, toUserId);
        return result > 0;
    }
    
    @Override
    public List<ChatMessage> getRecentMessages(Integer userId, Integer limit) {
        if (userId == null) {
            return null;
        }
        
        // 设置默认消息数量限制
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        
        // 调用Mapper查询最近聊天记录
        return chatMessageMapper.selectRecentMessages(userId, limit);
    }
    
    @Override
    public List<ChatMessage> getAllMessages() {
        return chatMessageMapper.selectAll();
    }
    
    @Override
    public boolean deleteConversation(Integer userId1, Integer userId2) {
        if (userId1 == null || userId2 == null) {
            return false;
        }
        
        // 调用Mapper删除两个用户之间的所有聊天消息
        int result = chatMessageMapper.deleteByUserIds(userId1, userId2);
        return result > 0;
    }
}