package com.longmai.datademo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.longmai.datademo.dao.mapper")
public class DataDemoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataDemoWebApplication.class, args);
    }

}
