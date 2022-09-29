package com.longmai.datademo.proxy;

public class Main {
    public static void main(String[] args) {
        TestMethod testMethod = new TestMethod();
//        testMethod.test();
        TestMethod testMethodProxy  = (TestMethod)new ProxyFactory(testMethod).getProxyInstance();
        testMethodProxy.test("西红柿");
    }
}
