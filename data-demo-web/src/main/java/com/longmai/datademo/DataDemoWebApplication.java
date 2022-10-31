package com.longmai.datademo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@MapperScan("com.longmai.datademo.dao.mapper")
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class DataDemoWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext runContext = SpringApplication.run(DataDemoWebApplication.class, args);
        System.err.println("############ 启动成功.....");
    }

}
