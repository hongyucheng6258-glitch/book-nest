package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户详情表 实体类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_info")
public class UserInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户详情ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.Auto)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别（0: 未知, 1: 男, 2: 女）
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private String birthday;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}