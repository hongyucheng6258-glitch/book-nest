package com.example.book.mapper;

import com.example.book.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface NotificationMapper {
    
    /**
     * 添加通知
     * @param notification 通知对象
     * @return 影响行数
     */
    int addNotification(Notification notification);
    
    /**
     * 根据用户ID获取通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> getNotificationsByUserId(@Param("userId") Integer userId);
    
    /**
     * 更新通知状态
     * @param id 通知ID
     * @param status 通知状态
     * @return 影响行数
     */
    int updateNotificationStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 根据通知ID删除通知
     * @param id 通知ID
     * @return 影响行数
     */
    int deleteNotificationById(@Param("id") Integer id);
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int getUnreadCount(@Param("userId") Integer userId);
    
    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     * @return 影响行数
     */
    int markAllAsRead(@Param("userId") Integer userId);
}