package com.example.book.config;

import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 标记为Spring配置类，让Spring扫描加载
public class MyBatisFlexConfig {

    /**
     * 自定义MyBatis-Flex配置
     * MyBatis-Flex 1.11.1版本已经内置了分页功能，无需额外配置分页拦截器
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 可以在这里添加自定义配置
            // 例如：配置驼峰命名映射等
            configuration.setMapUnderscoreToCamelCase(true);
        };
    }
}