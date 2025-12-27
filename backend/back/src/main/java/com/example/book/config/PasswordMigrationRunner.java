package com.example.book.config;

import com.example.book.entity.User;
import com.example.book.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 密码迁移运行器
 * 在应用启动时自动检查并迁移明文密码到BCrypt加密密码
 */
@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始检查用户密码安全性...");

        // 查询所有用户
        List<User> users = userMapper.selectListByQuery(new QueryWrapper());
        int migratedCount = 0;

        for (User user : users) {
            String password = user.getPassword();
            // 简单的检查：如果密码不是以$2a$开头，我们假设它是明文密码
            // BCrypt哈希通常以$2a$或$2b$开头，长度为60
            if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$")) {
                System.out.println("发现未加密密码，用户: " + user.getUsername());

                // 加密密码
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);

                // 更新数据库
                userMapper.update(user);
                migratedCount++;
            }
        }

        if (migratedCount > 0) {
            System.out.println("密码迁移完成，共迁移了 " + migratedCount + " 个用户的密码。");
        } else {
            System.out.println("所有用户密码均已加密，无需迁移。");
        }
    }
}
