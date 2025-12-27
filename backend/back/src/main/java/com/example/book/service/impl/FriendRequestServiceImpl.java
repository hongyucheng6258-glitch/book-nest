package com.example.book.service.impl;

import com.example.book.entity.Friend;
import com.example.book.entity.FriendRequest;
import com.example.book.mapper.FriendMapper;
import com.example.book.mapper.FriendRequestMapper;
import com.example.book.service.FriendRequestService;
import com.example.book.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 好友请求服务实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class FriendRequestServiceImpl implements FriendRequestService {
    
    @Autowired
    private FriendRequestMapper friendRequestMapper;
    
    @Autowired
    private FriendService friendService;
    
    @Override
    public boolean sendFriendRequest(FriendRequest friendRequest) {
        // 添加日志记录
        System.out.println("开始发送好友请求，请求信息: " + friendRequest);
        
        if (friendRequest == null || friendRequest.getFromUserId() == null || friendRequest.getToUserId() == null) {
            throw new IllegalArgumentException("请求对象或用户ID为空");
        }
        
        // 检查是否已经是好友
        if (friendService.isFriends(friendRequest.getFromUserId(), friendRequest.getToUserId())) {
            throw new IllegalArgumentException("已经是好友关系");
        }
        
        // 检查是否已经发送过好友请求
        FriendRequest existingRequest = friendRequestMapper.selectByUserIds(friendRequest.getFromUserId(), friendRequest.getToUserId());
        if (existingRequest != null) {
            throw new IllegalArgumentException("已经发送过好友请求");
        }
        
        // 设置创建时间和更新时间
        friendRequest.setCreatedAt(LocalDateTime.now());
        friendRequest.setUpdatedAt(LocalDateTime.now());
        friendRequest.setStatus(1); // 1-待处理
        
        // 调用Mapper发送好友请求
        int result = friendRequestMapper.insert(friendRequest);
        System.out.println("发送好友请求结果: " + (result > 0 ? "成功" : "失败"));
        return result > 0;
    }
    
    @Override
    public boolean acceptFriendRequest(Integer requestId) {
        if (requestId == null) {
            return false;
        }
        
        // 查询好友请求
        FriendRequest request = friendRequestMapper.selectById(requestId);
        if (request == null || request.getStatus() != 1) {
            return false;
        }
        
        // 创建好友关系
        Friend friend1 = new Friend();
        friend1.setUser1Id(request.getFromUserId());
        friend1.setUser2Id(request.getToUserId());
        friend1.setStatus(1); // 1-正常
        friend1.setCreatedAt(LocalDateTime.now());
        friend1.setUpdatedAt(LocalDateTime.now());
        
        if (!friendService.addFriend(friend1)) {
            return false;
        }
        
        // 更新好友请求状态为已接受
        int result = friendRequestMapper.updateStatus(requestId, 2); // 2-已接受
        return result > 0;
    }
    
    @Override
    public boolean rejectFriendRequest(Integer requestId) {
        if (requestId == null) {
            return false;
        }
        
        // 查询好友请求
        FriendRequest request = friendRequestMapper.selectById(requestId);
        if (request == null || request.getStatus() != 1) {
            return false;
        }
        
        // 更新好友请求状态为已拒绝
        int result = friendRequestMapper.updateStatus(requestId, 3); // 3-已拒绝
        return result > 0;
    }
    
    @Override
    public boolean deleteFriendRequest(Integer requestId) {
        if (requestId == null) {
            return false;
        }
        
        // 调用Mapper删除好友请求
        int result = friendRequestMapper.deleteById(requestId);
        return result > 0;
    }
    
    @Override
    public List<FriendRequest> getReceivedRequests(Integer toUserId) {
        if (toUserId == null) {
            return null;
        }
        
        // 调用Mapper查询收到的好友请求列表
        return friendRequestMapper.selectByToUserId(toUserId);
    }
    
    @Override
    public List<FriendRequest> getSentRequests(Integer fromUserId) {
        if (fromUserId == null) {
            return null;
        }
        
        // 调用Mapper查询发送的好友请求列表
        return friendRequestMapper.selectByFromUserId(fromUserId);
    }
    
    @Override
    public FriendRequest getFriendRequest(Integer fromUserId, Integer toUserId) {
        if (fromUserId == null || toUserId == null) {
            return null;
        }
        
        // 调用Mapper查询好友请求
        return friendRequestMapper.selectByUserIds(fromUserId, toUserId);
    }
    
    @Override
    public List<FriendRequest> getAllFriendRequests() {
        return friendRequestMapper.selectAll();
    }
}