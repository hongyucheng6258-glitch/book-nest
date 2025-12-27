package com.example.book.mapper;

import com.example.book.entity.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 好友关系表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface FriendMapper {
    
    /**
     * 添加好友关系
     * @param friend 好友关系对象
     * @return 影响行数
     */
    int insert(Friend friend);
    
    /**
     * 根据ID删除好友关系
     * @param id 好友关系ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据用户ID和好友ID删除好友关系
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 影响行数
     */
    int deleteByUserIdAndFriendId(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
    
    /**
     * 根据ID查询好友关系
     * @param id 好友关系ID
     * @return 好友关系对象
     */
    Friend selectById(@Param("id") Integer id);
    
    /**
     * 查询两个用户之间是否存在好友关系
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 好友关系对象
     */
    Friend selectByUserIds(@Param("user1Id") Integer user1Id, @Param("user2Id") Integer user2Id);
    
    /**
     * 查询用户的好友列表
     * @param userId 用户ID
     * @return 好友列表
     */
    List<Friend> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 更新好友关系状态
     * @param id 好友关系ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 获取所有好友关系
     * @return 好友关系列表
     */
    List<Friend> selectAll();
}