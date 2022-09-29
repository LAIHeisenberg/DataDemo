package com.longmai.datademo.proxy;

import org.springframework.cglib.proxy.Enhancer;

public class ProxyFactory<T> {

    private T target;

    public ProxyFactory(T target) {
        this.target = target;
    }

    // 创建代理对象

    public  Object getProxyInstance() {

        // 1.cglib工具类
        Enhancer en = new Enhancer();
        // 2.设置父类
        en.setSuperclass(this.target.getClass());
        // 3.设置回调函数
        en.setCallback(new TargetInterceptor());

        return en.create();
    }

}
