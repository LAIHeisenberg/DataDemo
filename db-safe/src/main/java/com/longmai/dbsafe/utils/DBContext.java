package com.longmai.dbsafe.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;


public class DBContext {

    public static ThreadLocal<DBInfo> dbContextThreadLocal = new ThreadLocal<>();

    public static void save(String host, String port, String dbName, String dbType, String user){
        DBInfo dbInfo = new DBInfo(host, port, dbName, dbType, user, null, null);
        dbContextThreadLocal.set(dbInfo);
    }

    public static void save(String host, String port, String dbName, String dbType, String user, String jdbcUrl, String passwd){
        DBInfo dbInfo = new DBInfo(host, port, dbName, dbType, user, jdbcUrl, passwd);
        dbContextThreadLocal.set(dbInfo);
    }

    public static void save(DBInfo dbInfo){
        if (Objects.nonNull(dbInfo)){
            dbContextThreadLocal.set(dbInfo);
        }
    }

    public static DBInfo get(){
        return dbContextThreadLocal.get();
    }

    @Data
    @AllArgsConstructor
    static class DBInfo{
        private String host;
        private String port;
        private String dbName;
        private String dbType;
        private String user;
        private String jdbcUrl;
        private String passwd;
    }
}
