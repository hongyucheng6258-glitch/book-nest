package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.JwtUtil;
import com.example.book.common.Result;
import com.example.book.entity.User;
import com.example.book.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        try {
            // 使用Spring Security的AuthenticationManager进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));

            // 认证成功，生成JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // 获取用户信息
            User user = userMapper
                    .selectOneByQuery(QueryWrapper.create().where(User::getUsername).eq(loginUser.getUsername()));

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("privilege", String.valueOf(user.getPrivilege()));

            return new Result<>(true, data, "登录成功");
        } catch (AuthenticationException e) {
            throw new BusinessException("用户名或密码错误");
        }
    }

    @GetMapping("/check")
    public Result<Map<String, String>> checkAuth(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        // 简单的token验证
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException("认证失败：未提供有效的认证信息");
        }

        String token = authorization.substring(7);
        String username = null;

        try {
            // 从Token中提取用户名
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            throw new BusinessException("认证失败：无效的令牌");
        }

        if (username == null) {
            throw new BusinessException("认证失败：无效的令牌");
        }

        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        return new Result<>(true, data, "认证成功");
    }

    /**
     * 修改密码
     * 
     * @param request       包含旧密码和新密码
     * @param authorization 请求头中的Authorization
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            // 验证Authorization头
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new BusinessException("认证失败：未提供有效的认证信息");
            }

            // 从Token中提取用户名
            String token = authorization.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null) {
                throw new BusinessException("认证失败：无效的令牌");
            }

            // 获取请求参数
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            // 验证参数
            if (oldPassword == null || oldPassword.isEmpty()) {
                throw new BusinessException("旧密码不能为空");
            }

            if (newPassword == null || newPassword.isEmpty()) {
                throw new BusinessException("新密码不能为空");
            }

            // 查询用户信息
            User user = userMapper.selectOneByQuery(QueryWrapper.create().where(User::getUsername).eq(username));

            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 验证旧密码
            // 使用Spring Security的UserDetailsService和AuthenticationManager来验证旧密码
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, oldPassword));
            } catch (AuthenticationException e) {
                throw new BusinessException("旧密码错误");
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userMapper.update(user);

            return new Result<>(true, true, "密码修改成功");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     * 
     * @param registerUser 包含用户名和密码的注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User registerUser) {
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper
                    .selectOneByQuery(QueryWrapper.create().where(User::getUsername).eq(registerUser.getUsername()));

            if (existingUser != null) {
                throw new BusinessException("用户名已存在");
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(registerUser.getUsername());
            newUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
            newUser.setPrivilege(0); // 默认普通用户权限

            // 保存用户到数据库
            userMapper.insert(newUser);

            // 返回注册结果
            Map<String, Object> data = new HashMap<>();
            data.put("id", newUser.getId());
            data.put("username", newUser.getUsername());

            return new Result<>(true, data, "注册成功");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("注册失败：" + e.getMessage());
        }
    }

    /**
     * 搜索用户
     * 
     * @param keyword 搜索关键字
     * @return 用户列表
     */
    @GetMapping("/user/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        try {
            System.out.println("搜索关键字: " + keyword);

            // 直接使用原生SQL查询，确保搜索逻辑正确
            List<User> users = userMapper.selectListByQuery(QueryWrapper.create()
                    .where(User::getUsername).like("%" + keyword + "%"));

            System.out.println("搜索结果数量: " + users.size());
            for (User user : users) {
                System.out.println("用户: " + user.getId() + ", " + user.getUsername() + ", " + user.getPrivilege());
            }

            return new Result<>(true, users, "搜索用户成功");
        } catch (Exception e) {
            System.out.println("搜索用户异常: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException("搜索用户失败：" + e.getMessage());
        }
    }
}