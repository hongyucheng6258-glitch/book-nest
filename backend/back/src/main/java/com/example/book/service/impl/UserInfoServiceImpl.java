package com.example.book.service.impl;

import com.example.book.entity.UserInfo;
import com.example.book.mapper.UserInfoMapper;
import com.example.book.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户详情表 Service 实现类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    /**
     * 根据用户ID查询用户详情
     */
    @Override
    public UserInfo getUserInfoByUserId(Integer userId) {
        logger.info("根据用户ID查询用户详情，userId: {}", userId);
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        logger.info("查询结果: {}", userInfo);
        return userInfo;
    }
    
    /**
     * 插入或更新用户详情
     */
    @Override
    public int saveOrUpdateUserInfo(UserInfo userInfo) {
        logger.info("插入或更新用户详情，userInfo: {}", userInfo);
        
        // 检查用户详情是否已存在
        UserInfo existingInfo = userInfoMapper.selectByUserId(userInfo.getUserId());
        logger.info("已存在的用户详情: {}", existingInfo);
        
        if (existingInfo != null) {
            // 如果已存在，更新
            userInfo.setId(existingInfo.getId());
            logger.info("执行更新操作，userInfo: {}", userInfo);
            int result = userInfoMapper.update(userInfo);
            logger.info("更新操作影响行数: {}", result);
            return result;
        } else {
            // 如果不存在，插入
            logger.info("执行插入操作，userInfo: {}", userInfo);
            int result = userInfoMapper.insert(userInfo);
            logger.info("插入操作影响行数: {}", result);
            return result;
        }
    }
    
    /**
     * 根据用户ID删除用户详情
     */
    @Override
    public int deleteUserInfoByUserId(Integer userId) {
        return userInfoMapper.deleteByUserId(userId);
    }
}