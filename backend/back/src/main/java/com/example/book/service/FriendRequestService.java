package com.example.book.service;

import com.example.book.entity.FriendRequest;

import java.util.List;

/**
 * 好友请求服务接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface FriendRequestService {
    
    /**
     * 发送好友请求
     * @param friendRequest 好友请求对象
     * @return 发送结果
     */
    boolean sendFriendRequest(FriendRequest friendRequest);
    
    /**
     * 接受好友请求
     * @param requestId 请求ID
     * @return 处理结果
     */
    boolean acceptFriendRequest(Integer requestId);
    
    /**
     * 拒绝好友请求
     * @param requestId 请求ID
     * @return 处理结果
     */
    boolean rejectFriendRequest(Integer requestId);
    
    /**
     * 删除好友请求
     * @param requestId 请求ID
     * @return 删除结果
     */
    boolean deleteFriendRequest(Integer requestId);
    
    /**
     * 查询用户收到的好友请求列表
     * @param toUserId 接收者ID
     * @return 好友请求列表
     */
    List<FriendRequest> getReceivedRequests(Integer toUserId);
    
    /**
     * 查询用户发送的好友请求列表
     * @param fromUserId 发送者ID
     * @return 好友请求列表
     */
    List<FriendRequest> getSentRequests(Integer fromUserId);
    
    /**
     * 查询两个用户之间是否存在好友请求
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 好友请求对象
     */
    FriendRequest getFriendRequest(Integer fromUserId, Integer toUserId);
    
    /**
     * 获取所有好友请求
     * @return 好友请求列表
     */
    List<FriendRequest> getAllFriendRequests();
}