package com.example.book.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 静态资源配置类
 * 用于配置静态资源访问路径
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadDir = System.getProperty("user.dir") + File.separator + "upload" + File.separator;
        
        // 映射头像访问路径
        registry.addResourceHandler("/upload/avatar/**")
                .addResourceLocations("file:" + uploadDir + "avatar/");
    }
}