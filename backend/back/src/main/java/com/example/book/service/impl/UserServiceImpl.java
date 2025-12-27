package com.example.book.service.impl;

import com.example.book.entity.User;
import com.example.book.mapper.UserMapper;
import com.example.book.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<User> listUsers(Map<String, Object> queryParams) {
        // 支持前端传递的currentPage和pageSize参数名
        int page = Integer.parseInt(queryParams.getOrDefault("currentPage", queryParams.getOrDefault("page", 1)).toString());
        int limit = Integer.parseInt(queryParams.getOrDefault("pageSize", queryParams.getOrDefault("limit", 10)).toString());
        // 修复获取关键字的代码，避免空指针异常
        Object keywordObj = queryParams.getOrDefault("searchQuery", queryParams.getOrDefault("keyword", null));
        String keyword = keywordObj != null ? keywordObj.toString() : "";
        // 添加权限筛选
        Object privilegeObj = queryParams.getOrDefault("privilege", null);

        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!keyword.isEmpty()) {
            // 修复like方法的语法，使用正确的MyBatis-Flex语法
            queryWrapper.and(User::getUsername).like(keyword);
        }
        // 权限筛选
        if (privilegeObj != null) {
            int privilege = Integer.parseInt(privilegeObj.toString());
            queryWrapper.eq("privilege", privilege);
        }

        return userMapper.paginate(page, limit, queryWrapper);
    }

    @Override
    public boolean deleteUser(Integer id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean resetPassword(Integer id, String newPassword) {
        User user = userMapper.selectOneById(id);
        if (user == null) return false;
        // 使用BCryptPasswordEncoder加密密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
        return true;
    }

    @Override
    public boolean addUser(User user) {
        // 使用BCryptPasswordEncoder加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        int rowsInserted = userMapper.insert(user);
        return rowsInserted > 0;
    }

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        QueryWrapper queryWrapper = QueryWrapper.create().eq("username", username);
        User user = userMapper.selectOneByQuery(queryWrapper);
        
        // 验证用户存在且密码正确
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }
}