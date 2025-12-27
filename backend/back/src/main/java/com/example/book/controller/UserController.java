package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.User;
import com.example.book.service.UserService;
import com.mybatisflex.core.paginate.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Result<java.util.List<User>> getUsers(@RequestParam Map<String, Object> queryParams) {
        Page<User> pageResult = userService.listUsers(queryParams);
        // 将分页结果转换为前端期望的格式
        return new Result<>(true, pageResult.getRecords(), "查询用户列表成功");
    }

    @DeleteMapping("/users/{id}")
    public Result<Boolean> deleteUser(@PathVariable Integer id) {
        boolean success = userService.deleteUser(id);
        if (!success) {
            throw new BusinessException("删除用户失败：用户不存在");
        }
        return new Result<>(true, success, "删除用户成功");
    }

    @PutMapping("/users/{id}/password")
    public Result<Boolean> resetPassword(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException("新密码不能为空");
        }
        boolean success = userService.resetPassword(id, newPassword);
        if (!success) {
            throw new BusinessException("修改密码失败：用户不存在");
        }
        return new Result<>(true, success, "密码修改成功");
    }

    @PostMapping("/users")
    public Result<Boolean> addUser(@RequestBody User user) {
        boolean success = userService.addUser(user);
        if (!success) {
            throw new BusinessException("添加用户失败");
        }
        return new Result<>(true, success, "添加用户成功");
    }
}