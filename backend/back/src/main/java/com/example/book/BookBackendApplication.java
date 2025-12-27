package com.example.book;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.book.mapper") // 扫描Mapper接口所在包
public class BookBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookBackendApplication.class, args);
    }
}