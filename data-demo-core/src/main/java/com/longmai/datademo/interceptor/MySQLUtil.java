package com.longmai.datademo.interceptor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLUtil {
    Connection connection = null;
    public MySQLUtil(){
        try{
            Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
            connection = DriverManager.getConnection(
                    "jdbc:log4jdbc:mysql://192.168.1.128:3306/eladmin?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true",
                    "laiyz","laiyz123!");

        }catch(ClassNotFoundException e){
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MySQLUtil test1(){
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("select * from ddm_user where user_name='admin'");
            preparedStatement.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return this;
    }

    static int counter = 0;
    public MySQLUtil test2(){
        try{
            counter++;
            PreparedStatement preparedStatement = connection.prepareStatement("insert into test_ids(id,dh) values('"+counter+"','"+counter+"');");
            preparedStatement.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return this;
    }

}
