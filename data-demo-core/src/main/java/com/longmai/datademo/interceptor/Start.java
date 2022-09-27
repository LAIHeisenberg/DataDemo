package com.longmai.datademo.interceptor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Start implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        new JavassistInjector().start();
    }
}
