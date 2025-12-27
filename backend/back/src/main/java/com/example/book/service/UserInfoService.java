package com.example.book.service;

import com.example.book.entity.UserInfo;

/**
 * 用户详情表 Service 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface UserInfoService {
    
    /**
     * 根据用户ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserInfo getUserInfoByUserId(Integer userId);
    
    /**
     * 插入或更新用户详情
     *
     * @param userInfo 用户详情
     * @return 影响行数
     */
    int saveOrUpdateUserInfo(UserInfo userInfo);
    
    /**
     * 根据用户ID删除用户详情
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserInfoByUserId(Integer userId);
}