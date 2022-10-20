package com.longmai.datademo;

import com.longmai.datademo.utils.SpringContextHolder;
import com.longmai.dbsafe.utils.FeignClientContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;


@MapperScan("com.longmai.datademo.dao.mapper")
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages={"com.longmai.datakeeper.rest"})
public class DataDemoWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext runContext = SpringApplication.run(DataDemoWebApplication.class, args);
        FeignClientContext.setApplicationContext(runContext);
        System.err.println("############ 启动成功.....");
    }

//    @Bean
//    public SpringContextHolder springContextHolder() {
//        return new SpringContextHolder();
//    }
}
