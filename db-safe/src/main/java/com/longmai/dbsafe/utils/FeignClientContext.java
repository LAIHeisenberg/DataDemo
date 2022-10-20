package com.longmai.dbsafe.utils;

import com.longmai.datakeeper.rest.db.DBEncryptHandler;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

public class FeignClientContext{

    private static FeignClientBuilder builder;

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        builder = new FeignClientBuilder(applicationContext);
    }

    public static <T>  T buildClient(Class<T> clazz, String name){
        return builder.forType(clazz, name).build();
    }

}
