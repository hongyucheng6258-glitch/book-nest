package com.example.book.service.impl;

import com.example.book.entity.Friend;
import com.example.book.mapper.FriendMapper;
import com.example.book.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 好友服务实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class FriendServiceImpl implements FriendService {
    
    @Autowired
    private FriendMapper friendMapper;
    
    @Override
    public boolean addFriend(Friend friend) {
        if (friend == null || friend.getUser1Id() == null || friend.getUser2Id() == null) {
            return false;
        }
        
        // 设置创建时间和更新时间
        friend.setCreatedAt(LocalDateTime.now());
        friend.setUpdatedAt(LocalDateTime.now());
        
        // 调用Mapper添加好友关系
        int result = friendMapper.insert(friend);
        return result > 0;
    }
    
    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        if (userId == null || friendId == null) {
            return false;
        }
        
        // 调用Mapper删除好友关系
        int result = friendMapper.deleteByUserIdAndFriendId(userId, friendId);
        return result > 0;
    }
    
    @Override
    public boolean isFriends(Integer userId1, Integer userId2) {
        if (userId1 == null || userId2 == null) {
            return false;
        }
        
        // 调用Mapper查询好友关系
        Friend friend = friendMapper.selectByUserIds(userId1, userId2);
        return friend != null && friend.getStatus() == 1;
    }
    
    @Override
    public List<Friend> getFriendsByUserId(Integer userId) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        
        // 调用Mapper查询好友列表
        return friendMapper.selectByUserId(userId) != null ? friendMapper.selectByUserId(userId) : java.util.Collections.emptyList();
    }
    
    @Override
    public List<Friend> getAllFriends() {
        return friendMapper.selectAll();
    }
}