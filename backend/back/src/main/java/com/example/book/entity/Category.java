package com.example.book.entity;

import lombok.Data;

@Data // lombok注解：自动生成getter、setter、toString
public class Category {
    private Integer id;     // 分类ID
    private String name;    // 分类名称
}