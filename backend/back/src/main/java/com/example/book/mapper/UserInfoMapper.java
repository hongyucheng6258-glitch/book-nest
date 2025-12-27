package com.example.book.mapper;

import com.example.book.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户详情表 Mapper 接口。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface UserInfoMapper {
    
    /**
     * 根据用户ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserInfo selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 插入用户详情
     *
     * @param userInfo 用户详情
     * @return 影响行数
     */
    int insert(UserInfo userInfo);
    
    /**
     * 更新用户详情
     *
     * @param userInfo 用户详情
     * @return 影响行数
     */
    int update(UserInfo userInfo);
    
    /**
     * 根据用户ID删除用户详情
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Integer userId);
}