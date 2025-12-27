package com.example.book.mapper;

import com.example.book.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface ChatMessageMapper {
    
    /**
     * 添加聊天消息
     * @param chatMessage 聊天消息对象
     * @return 影响行数
     */
    int insert(ChatMessage chatMessage);
    
    /**
     * 根据ID删除聊天消息
     * @param id 消息ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据ID查询聊天消息
     * @param id 消息ID
     * @return 聊天消息对象
     */
    ChatMessage selectById(@Param("id") Integer id);
    
    /**
     * 查询两个用户之间的聊天消息
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param limit 消息数量限制
     * @return 聊天消息列表
     */
    List<ChatMessage> selectByUserIds(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2, @Param("limit") Integer limit);
    
    /**
     * 查询用户的未读消息数量
     * @param toUserId 接收者ID
     * @return 未读消息数量
     */
    int selectUnreadCount(@Param("toUserId") Integer toUserId);
    
    /**
     * 更新消息为已读
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 影响行数
     */
    int updateReadStatus(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
    
    /**
     * 查询用户的最近聊天记录
     * @param userId 用户ID
     * @param limit 消息数量限制
     * @return 聊天消息列表
     */
    List<ChatMessage> selectRecentMessages(@Param("userId") Integer userId, @Param("limit") Integer limit);
    
    /**
     * 获取所有聊天消息
     * @return 聊天消息列表
     */
    List<ChatMessage> selectAll();
    
    /**
     * 删除两个用户之间的所有聊天消息
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 影响行数
     */
    int deleteByUserIds(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
}