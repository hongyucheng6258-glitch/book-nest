package com.example.book.mapper;

import com.example.book.entity.FriendRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 好友请求表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface FriendRequestMapper {
    
    /**
     * 添加好友请求
     * @param friendRequest 好友请求对象
     * @return 影响行数
     */
    int insert(FriendRequest friendRequest);
    
    /**
     * 根据ID删除好友请求
     * @param id 好友请求ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据发送者和接收者ID删除好友请求
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 影响行数
     */
    int deleteByUserIds(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
    
    /**
     * 根据ID查询好友请求
     * @param id 好友请求ID
     * @return 好友请求对象
     */
    FriendRequest selectById(@Param("id") Integer id);
    
    /**
     * 查询发送者和接收者之间的好友请求
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 好友请求对象
     */
    FriendRequest selectByUserIds(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
    
    /**
     * 查询用户收到的好友请求列表
     * @param toUserId 接收者ID
     * @return 好友请求列表
     */
    List<FriendRequest> selectByToUserId(@Param("toUserId") Integer toUserId);
    
    /**
     * 查询用户发送的好友请求列表
     * @param fromUserId 发送者ID
     * @return 好友请求列表
     */
    List<FriendRequest> selectByFromUserId(@Param("fromUserId") Integer fromUserId);
    
    /**
     * 更新好友请求状态
     * @param id 好友请求ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 获取所有好友请求
     * @return 好友请求列表
     */
    List<FriendRequest> selectAll();
}