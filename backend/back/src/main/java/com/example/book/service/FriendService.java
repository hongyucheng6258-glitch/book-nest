package com.example.book.service;

import com.example.book.entity.Friend;

import java.util.List;

/**
 * 好友服务接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface FriendService {
    
    /**
     * 添加好友关系
     * @param friend 好友关系对象
     * @return 添加结果
     */
    boolean addFriend(Friend friend);
    
    /**
     * 删除好友关系
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 删除结果
     */
    boolean deleteFriend(Integer userId, Integer friendId);
    
    /**
     * 查询两个用户之间是否存在好友关系
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 是否为好友
     */
    boolean isFriends(Integer userId1, Integer userId2);
    
    /**
     * 查询用户的好友列表
     * @param userId 用户ID
     * @return 好友列表
     */
    List<Friend> getFriendsByUserId(Integer userId);
    
    /**
     * 获取所有好友关系
     * @return 好友关系列表
     */
    List<Friend> getAllFriends();
}