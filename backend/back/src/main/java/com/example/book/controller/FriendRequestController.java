package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.FriendRequest;
import com.example.book.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友请求控制器
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/friend/request")
public class FriendRequestController {
    
    @Autowired
    private FriendRequestService friendRequestService;
    
    /**
     * 发送好友请求
     * @param friendRequest 好友请求对象
     * @return 响应结果
     */
    @PostMapping("/send")
    public Result<?> sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        try {
            boolean result = friendRequestService.sendFriendRequest(friendRequest);
            if (result) {
                return new Result<>(true, "发送好友请求成功");
            } else {
                return new Result<>(false, "发送好友请求失败");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("发送好友请求失败，详细原因: " + e.getMessage());
            return new Result<>(false, "发送好友请求失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("发送好友请求失败，异常: " + e.getMessage());
            e.printStackTrace();
            return new Result<>(false, "发送好友请求失败: " + e.getMessage());
        }
    }
    
    /**
     * 接受好友请求
     * @param requestId 请求ID
     * @return 响应结果
     */
    @PostMapping("/accept/{requestId}")
    public Result<?> acceptFriendRequest(@PathVariable Integer requestId) {
        boolean result = friendRequestService.acceptFriendRequest(requestId);
        if (result) {
            return new Result<>(true, "接受好友请求成功");
        } else {
            return new Result<>(false, "接受好友请求失败");
        }
    }
    
    /**
     * 拒绝好友请求
     * @param requestId 请求ID
     * @return 响应结果
     */
    @PostMapping("/reject/{requestId}")
    public Result<?> rejectFriendRequest(@PathVariable Integer requestId) {
        boolean result = friendRequestService.rejectFriendRequest(requestId);
        if (result) {
            return new Result<>(true, "拒绝好友请求成功");
        } else {
            return new Result<>(false, "拒绝好友请求失败");
        }
    }
    
    /**
     * 查询用户收到的好友请求列表
     * @param toUserId 接收者ID
     * @return 响应结果
     */
    @GetMapping("/received/{toUserId}")
    public Result<?> getReceivedRequests(@PathVariable Integer toUserId) {
        List<FriendRequest> requests = friendRequestService.getReceivedRequests(toUserId);
        return new Result<>(requests);
    }
    
    /**
     * 查询用户发送的好友请求列表
     * @param fromUserId 发送者ID
     * @return 响应结果
     */
    @GetMapping("/sent/{fromUserId}")
    public Result<?> getSentRequests(@PathVariable Integer fromUserId) {
        List<FriendRequest> requests = friendRequestService.getSentRequests(fromUserId);
        return new Result<>(requests);
    }
    
    /**
     * 删除好友请求
     * @param requestId 请求ID
     * @return 响应结果
     */
    @DeleteMapping("/delete/{requestId}")
    public Result<?> deleteFriendRequest(@PathVariable Integer requestId) {
        boolean result = friendRequestService.deleteFriendRequest(requestId);
        if (result) {
            return new Result<>(true, "删除好友请求成功");
        } else {
            return new Result<>(false, "删除好友请求失败");
        }
    }
    
    /**
     * 获取所有好友请求（管理员用）
     * @return 响应结果
     */
    @GetMapping("/all")
    public Result<?> getAllFriendRequests() {
        List<FriendRequest> requests = friendRequestService.getAllFriendRequests();
        return new Result<>(requests);
    }
}