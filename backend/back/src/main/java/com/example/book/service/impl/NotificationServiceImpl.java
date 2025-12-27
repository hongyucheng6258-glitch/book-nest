package com.example.book.service.impl;

import com.example.book.entity.Notification;
import com.example.book.mapper.NotificationMapper;
import com.example.book.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知 Service 实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public boolean addNotification(Notification notification) {
        if (notification == null || notification.getUserId() == null) {
            return false;
        }
        
        int result = notificationMapper.addNotification(notification);
        return result > 0;
    }
    
    @Override
    public List<Notification> getNotifications(Integer userId) {
        if (userId == null) {
            return null;
        }
        
        return notificationMapper.getNotificationsByUserId(userId);
    }
    
    @Override
    public boolean updateNotificationStatus(Integer id, Integer status) {
        if (id == null || status == null) {
            return false;
        }
        
        int result = notificationMapper.updateNotificationStatus(id, status);
        return result > 0;
    }
    
    @Override
    public boolean deleteNotification(Integer id) {
        if (id == null) {
            return false;
        }
        
        int result = notificationMapper.deleteNotificationById(id);
        return result > 0;
    }
    
    @Override
    public int getUnreadCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        
        return notificationMapper.getUnreadCount(userId);
    }
    
    @Override
    public boolean markAllAsRead(Integer userId) {
        if (userId == null) {
            return false;
        }
        
        int result = notificationMapper.markAllAsRead(userId);
        return result > 0;
    }
    
    @Override
    public boolean sendOrderNotification(Integer userId, Integer orderId, String content) {
        if (userId == null || orderId == null || content == null) {
            return false;
        }
        
        Notification notification = Notification.builder()
                .userId(userId)
                .orderId(orderId)
                .content(content)
                .status(0) // 0: 未读
                .createdAt(LocalDateTime.now())
                .type(0) // 0: 订单通知
                .build();
        
        return addNotification(notification);
    }
}