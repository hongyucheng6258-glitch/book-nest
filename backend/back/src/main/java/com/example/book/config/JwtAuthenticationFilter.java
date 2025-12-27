package com.example.book.config;

import com.example.book.common.JwtUtil;
import com.example.book.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // 从请求头中获取Authorization字段
        String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        // 检查Authorization头是否存在，并且格式正确
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                // 从Token中提取用户名
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                // Token解析失败，不做处理，继续过滤链
                System.err.println("JWT Token解析失败: " + e.getMessage());
            }
        }
        
        // 如果用户名不为空，且SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 验证Token
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                // 创建认证Token
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // 设置认证详情
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 将认证信息设置到SecurityContext中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}