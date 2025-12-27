package com.example.book.service;

import com.example.book.entity.User;
import com.mybatisflex.core.paginate.Page;
import java.util.Map;

public interface UserService {
    Page<User> listUsers(Map<String, Object> queryParams);
    boolean deleteUser(Integer id);
    boolean resetPassword(Integer id, String newPassword);
    boolean addUser(User user);
    User login(String username, String password);
}