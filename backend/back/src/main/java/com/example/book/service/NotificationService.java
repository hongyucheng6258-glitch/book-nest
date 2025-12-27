package com.example.book.service;

import com.example.book.entity.Notification;

import java.util.List;

/**
 * 通知 Service 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface NotificationService {
    
    /**
     * 添加通知
     * @param notification 通知对象
     * @return 操作结果
     */
    boolean addNotification(Notification notification);
    
    /**
     * 根据用户ID获取通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> getNotifications(Integer userId);
    
    /**
     * 更新通知状态
     * @param id 通知ID
     * @param status 通知状态
     * @return 操作结果
     */
    boolean updateNotificationStatus(Integer id, Integer status);
    
    /**
     * 根据通知ID删除通知
     * @param id 通知ID
     * @return 操作结果
     */
    boolean deleteNotification(Integer id);
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int getUnreadCount(Integer userId);
    
    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean markAllAsRead(Integer userId);
    
    /**
     * 发送订单状态通知
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param content 通知内容
     * @return 操作结果
     */
    boolean sendOrderNotification(Integer userId, Integer orderId, String content);
}