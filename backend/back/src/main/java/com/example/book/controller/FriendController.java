package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.Friend;
import com.example.book.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友控制器
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/friend")
public class FriendController {
    
    @Autowired
    private FriendService friendService;
    
    /**
     * 查询用户的好友列表
     * @param userId 用户ID
     * @return 响应结果
     */
    @GetMapping("/list/{userId}")
    public Result<?> getFriendsList(@PathVariable Integer userId) {
        List<Friend> friends = friendService.getFriendsByUserId(userId);
        return new Result<>(friends);
    }
    
    /**
     * 删除好友关系
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 响应结果
     */
    @DeleteMapping("/delete")
    public Result<?> deleteFriend(@RequestParam Integer userId, @RequestParam Integer friendId) {
        boolean result = friendService.deleteFriend(userId, friendId);
        if (result) {
            return new Result<>(true, "删除好友成功");
        } else {
            return new Result<>(false, "删除好友失败");
        }
    }
    
    /**
     * 检查两个用户是否为好友
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 响应结果
     */
    @GetMapping("/check")
    public Result<?> checkFriendship(@RequestParam Integer userId1, @RequestParam Integer userId2) {
        boolean isFriend = friendService.isFriends(userId1, userId2);
        return new Result<>(isFriend);
    }
    
    /**
     * 获取所有好友关系（管理员用）
     * @return 响应结果
     */
    @GetMapping("/all")
    public Result<?> getAllFriends() {
        List<Friend> friends = friendService.getAllFriends();
        return new Result<>(friends);
    }
    
    /**
     * 添加好友关系
     * @param friend 好友关系对象
     * @return 响应结果
     */
    @PostMapping("/add")
    public Result<?> addFriend(@RequestBody Friend friend) {
        boolean result = friendService.addFriend(friend);
        if (result) {
            return new Result<>(true, "添加好友关系成功");
        } else {
            return new Result<>(false, "添加好友关系失败");
        }
    }
}