package com.example.book;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 代码生成器（位于测试目录）
 * 用于生成MyBatis-Flex相关实体类、Mapper、Service和Controller
 */
public class CodeGenerator {

    public static void main(String[] args) {
        // 1. 配置数据源（注意数据库名是否正确，之前创建的是bookdb，这里保持一致）
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/book_db?serverTimezone=Asia/Shanghai&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("666666");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 2. 创建全局配置（使用链式风格）
        GlobalConfig globalConfig = createGlobalConfig();

        // 3. 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 4. 执行生成
        generator.generate();

        System.out.println("代码生成完成！");
    }

    /**

     */
    public static GlobalConfig createGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();

        // 基础包配置（所有生成的类都会基于这个基础包）
        String basePackage = "com.example.book";

        // 设置路径及包结构（使用点分隔符，符合Java包命名规范）
        globalConfig.getPackageConfig()
                .setSourceDir("src/main/java")  // 生成到主代码目录
                .setBasePackage(basePackage)    // 基础包路径
                .setEntityPackage(basePackage + ".entity")       // 实体类包：com.example.bookapp.entity
                .setMapperPackage(basePackage + ".mapper")       // Mapper接口包：com.example.bookapp.mapper
                .setServicePackage(basePackage + ".service")     // Service接口包：com.example.bookapp.service
                .setServiceImplPackage(basePackage + ".service.impl")  // Service实现包：com.example.bookapp.service.impl
                .setControllerPackage(basePackage + ".controller")   // Controller包：com.example.bookapp.controller
                .setTableDefPackage(basePackage + ".table")       // 表定义类包：com.example.bookapp.table
                .setMapperXmlPath("src/main/resources/mapper");   // MapperXML文件路径

        // 设置生成策略（指定要生成的表）
        globalConfig.getStrategyConfig()
                .setGenerateTable("book");  // 只生成book表的代码
        // 如需生成多个表，用逗号分隔：.setGenerateTable("book", "student")

        // 设置javadoc信息
        globalConfig.getJavadocConfig()
                .setAuthor("BookApp Developer") // 作者信息
                .setSince("1.0.0"); // 版本信息

        // 实体类配置
        globalConfig.enableEntity()
                .setWithLombok(true)  // 使用Lombok简化代码（需要Lombok依赖）
                .setJdkVersion(17) ;   // 适配JDK17


        // 启用表定义类生成
        globalConfig.enableTableDef();

        // Mapper接口配置
        globalConfig.enableMapper()
                .setMapperAnnotation(true);  // 生成@Mapper注解

        // 启用MapperXML文件生成
        globalConfig.enableMapperXml();

        // Service接口配置
        globalConfig.enableService();

        // Service实现类配置
        globalConfig.enableServiceImpl();

        // Controller配置
        globalConfig.enableController()
                .setRestStyle(true);  // 生成REST风格的Controller（使用@RestController）

        return globalConfig;
    }
}