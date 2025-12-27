package com.example.book.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Jackson配置类
 * 用于配置Jackson的序列化和反序列化行为
 */
@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java 8时间模块，支持LocalDateTime等新时间类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 配置LocalDateTime序列化格式
        javaTimeModule.addSerializer(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        
        // 禁用循环引用检测，使用@JsonIdentityInfo注解来处理循环引用
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        return objectMapper;
    }
}