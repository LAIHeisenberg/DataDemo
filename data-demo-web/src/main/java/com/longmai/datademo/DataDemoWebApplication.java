package com.longmai.datademo;

import com.longmai.datademo.utils.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;


@MapperScan("com.longmai.datademo.dao.mapper")
@EnableAsync
@RestController
@SpringBootApplication
@EnableTransactionManagement
public class DataDemoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataDemoWebApplication.class, args);
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
