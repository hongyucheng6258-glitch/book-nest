package com.example.book.common;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * JWT工具类，用于生成和验证JWT令牌。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Component
public class JwtUtil {

    // Token过期时间（24小时）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    
    // 生成Token
    public String generateToken(UserDetails userDetails) {
        // 使用简单的UUID + 用户名 + 时间戳生成token
        return UUID.randomUUID().toString().replace("-", "") + "." + userDetails.getUsername() + "." + System.currentTimeMillis();
    }
    
    // 验证Token
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    // 从Token中提取用户名
    public String extractUsername(String token) {
        if (token == null || !token.contains(".")) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        return parts[1];
    }
    
    // 检查Token是否过期
    private boolean isTokenExpired(String token) {
        if (token == null || !token.contains(".")) {
            return true;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return true;
        }
        try {
            long timestamp = Long.parseLong(parts[2]);
            return System.currentTimeMillis() - timestamp > EXPIRATION_TIME;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}