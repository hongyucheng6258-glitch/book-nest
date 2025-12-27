package com.example.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

/**
 * 用户表 实体类。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    @Id(keyType = com.mybatisflex.annotation.KeyType.None)
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 权限（1: 管理员, 0: 普通用户）
     */
    private Integer privilege;
    
    // 关联用户详情（非数据库字段）
    private UserInfo userInfo;
}