package com.internship;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// TODO: 排除SpringSecurity自动配置，因为我们使用自定义JWT认证
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.internship.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("========================================");
        System.out.println("  校企协同实习就业管理系统启动成功！");
        System.out.println("  访问地址: http://localhost:8080/login.html");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
