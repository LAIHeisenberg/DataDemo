package com.longmai.datademo.proxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TargetInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("代理 调用前");
        objects[0] = objects[0]+"后缀";
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println("代理 调用后");
        return result;
    }
}
