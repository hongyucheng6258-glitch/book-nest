package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.Notification;
import com.example.book.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知 Controller
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 根据用户ID获取通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    @GetMapping("/user")
    public Result<List<Notification>> getNotifications(@RequestParam Integer userId) {
        List<Notification> notifications = notificationService.getNotifications(userId);
        return new Result<>(notifications);
    }
    
    /**
     * 更新通知状态
     * @param id 通知ID
     * @param status 通知状态
     * @return 操作结果
     */
    @PutMapping("/read/{id}")
    public Result<?> updateNotificationStatus(@PathVariable Integer id, @RequestParam Integer status) {
        boolean result = notificationService.updateNotificationStatus(id, status);
        if (result) {
            return new Result<>(true, "更新通知状态成功");
        } else {
            return new Result<>(false, "更新通知状态失败");
        }
    }
    
    /**
     * 根据通知ID删除通知
     * @param id 通知ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteNotification(@PathVariable Integer id) {
        boolean result = notificationService.deleteNotification(id);
        if (result) {
            return new Result<>(true, "删除通知成功");
        } else {
            return new Result<>(false, "删除通知失败");
        }
    }
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<?> getUnreadCount(@RequestParam Integer userId) {
        int count = notificationService.getUnreadCount(userId);
        return new Result<>(count);
    }
    
    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/read-all")
    public Result<?> markAllAsRead(@RequestParam Integer userId) {
        boolean result = notificationService.markAllAsRead(userId);
        if (result) {
            return new Result<>(true, "标记所有通知为已读成功");
        } else {
            return new Result<>(false, "标记所有通知为已读失败");
        }
    }
}