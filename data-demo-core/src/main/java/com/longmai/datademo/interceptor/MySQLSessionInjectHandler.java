package com.longmai.datademo.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MySQLSessionInjectHandler {
    public static void getSQL(Object nativeSession,Object query,String queryString){
        if(query == null){
            return;
        }
        String strSQL = query.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date());
       // System.out.println("<<<拦截到SQL "+strSQL+",当前线程:"+Thread.currentThread()+",时间:"+dateStr+" >>>");
    }
    public static String  getSQL2(Object nativeSession,Object query,String queryString){
        if(query == null){
            return null;
        }
        String strSQL = queryString+":1111111";
        System.out.println("<<<拦截到query.toString() "+query.toString()+",当前线程:"+Thread.currentThread()+",修改后sql:"+strSQL+" >>>");
        return strSQL;
    }
}
