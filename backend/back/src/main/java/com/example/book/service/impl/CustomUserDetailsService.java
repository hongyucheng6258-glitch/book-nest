package com.example.book.service.impl;

import com.example.book.entity.User;
import com.example.book.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        QueryWrapper queryWrapper = QueryWrapper.create().eq("username", username);
        User user = userMapper.selectOneByQuery(queryWrapper);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        
        // 将用户权限转换为Spring Security的GrantedAuthority对象
        // 这里简化处理，根据privilege字段设置权限
        String authority = "ROLE_USER";
        if (user.getPrivilege() == 1) {
            authority = "ROLE_ADMIN";
        }
        
        // 创建并返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );
    }
}