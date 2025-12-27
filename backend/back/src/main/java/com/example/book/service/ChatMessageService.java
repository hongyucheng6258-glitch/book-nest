package com.example.book.service;

import com.example.book.entity.ChatMessage;

import java.util.List;

/**
 * 聊天消息服务接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface ChatMessageService {
    
    /**
     * 发送聊天消息
     * @param chatMessage 聊天消息对象
     * @return 发送结果
     */
    boolean sendMessage(ChatMessage chatMessage);
    
    /**
     * 删除聊天消息
     * @param messageId 消息ID
     * @return 删除结果
     */
    boolean deleteMessage(Integer messageId);
    
    /**
     * 查询两个用户之间的聊天消息
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param limit 消息数量限制
     * @return 聊天消息列表
     */
    List<ChatMessage> getChatHistory(Integer userId1, Integer userId2, Integer limit);
    
    /**
     * 查询用户的未读消息数量
     * @param toUserId 接收者ID
     * @return 未读消息数量
     */
    int getUnreadCount(Integer toUserId);
    
    /**
     * 标记消息为已读
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 处理结果
     */
    boolean markAsRead(Integer fromUserId, Integer toUserId);
    
    /**
     * 查询用户的最近聊天记录
     * @param userId 用户ID
     * @param limit 消息数量限制
     * @return 聊天消息列表
     */
    List<ChatMessage> getRecentMessages(Integer userId, Integer limit);
    
    /**
     * 获取所有聊天消息
     * @return 聊天消息列表
     */
    List<ChatMessage> getAllMessages();
    
    /**
     * 删除两个用户之间的所有聊天消息
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 删除结果
     */
    boolean deleteConversation(Integer userId1, Integer userId2);
}