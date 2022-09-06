package com.longmai.datademo.test;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MybatisGenerator {

    private final String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:3306/eladmin?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true";
    private final String username = "laiyz";
    private final String password = "laiyz123!";
    private final String outputJavaDir = "/home/laiyz/桌面/java";
    private final String parentPackageName = "com.longmai.datademo.dao";
    private final String outputMapperXmlDir = "/home/laiyz/桌面/xml";

    @Test
    public void gen(){

        FastAutoGenerator.create(jdbcUrl, username, password)
                .globalConfig(builder -> {
                    builder.author("longmai") // 设置作者
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(outputJavaDir); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(parentPackageName) // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, outputMapperXmlDir)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("ddm_user").addInclude("ddm_menu").addInclude("ddm_role") // 设置需要生成的表名
                            .addTablePrefix("ddm_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}
