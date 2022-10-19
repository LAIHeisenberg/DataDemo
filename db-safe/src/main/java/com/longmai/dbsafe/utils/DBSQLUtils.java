package com.longmai.dbsafe.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;

import lombok.Data;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBSQLUtils {

    private static final String INSERT_INTO_REGEX = "^INSERT INTO (\\w+) (.+,.*)VALUES(.*)";
    private static final String SELECT_REGEX = "^SELECT (.*) FROM (.*) WHERE (.*)";
    private static final String sql2 = "SELECT  id,user_name,nick_name,email,phone,gender,password,auth_method,admin_flag,dn,cert,enabled,create_time,update_time  FROM ddm_user \n" +
            "\n" +
            "\n" +
            "WHERE  user_name='admin'";


    public static void main(String[] args){
        
        String sql = "SELECT id, user_name, nick_name, email, phone\n" +
                "\t, gender, password, auth_method, admin_flag, dn\n" +
                "\t, cert, enabled, create_time, update_time\n" +
                "FROM ddm_user\n" +
                "WHERE user_name = 'admin' and create_time in (1,5) and enabled = true or auth_method > 2  and phone = 1234123512";

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelect select = sqlSelectStatement.getSelect();
        SQLSelectQueryBlock selectQueryBlock = select.getQueryBlock();
        Map<String, SQLExpr> map = new HashMap<>();
        fun((SQLBinaryOpExpr)selectQueryBlock.getWhere(), map);

        List<SQLExpr> split = SQLUtils.split((SQLBinaryOpExpr) selectQueryBlock.getWhere());
        SQLBinaryOpExpr sqlExpr = (SQLBinaryOpExpr) split.get(1);
        List<SQLExpr> split2 = SQLUtils.split((SQLBinaryOpExpr) sqlExpr.getLeft());

        Map<String, String> stringStringMap = extractTableColumnName(sql2);

        Map<String, SQLExpr> map2 = new HashMap<>();
        fun2((SQLBinaryOpExpr)selectQueryBlock.getWhere(), map2);
        System.out.println("111");
    }


    public static void fun2(SQLBinaryOpExpr parentLeft, Map<String, SQLExpr> map){
        List<SQLExpr> split = SQLUtils.split(parentLeft);
        for (SQLExpr sqlExpr : split){
            if (sqlExpr instanceof SQLBinaryOpExpr){
                fun2((SQLBinaryOpExpr) sqlExpr, map);
            }else {
                if (sqlExpr instanceof SQLInListExpr){
                    SQLInListExpr sqlInListExpr = (SQLInListExpr) sqlExpr;
                    SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlInListExpr.getExpr();
                    map.put(sqlIdentifierExpr.getName(), sqlInListExpr);

                }else if (sqlExpr instanceof SQLIdentifierExpr){
                    SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlExpr;
                    map.put(sqlIdentifierExpr.getName(), (SQLBinaryOpExpr) sqlIdentifierExpr.getParent());
                    return;
                }

            }
        }
    }


    public static void fun(SQLBinaryOpExpr parentLeft, Map<String, SQLExpr> map){
        if (parentLeft.getLeft() instanceof SQLBinaryOpExpr){
            fun((SQLBinaryOpExpr) parentLeft.getLeft(), map);
        } else if (parentLeft.getLeft() instanceof  SQLIdentifierExpr){
            map.put(parentLeft.getLeft().toString(), parentLeft);
            return;
        }else if (parentLeft.getLeft() instanceof SQLInListExpr){
            SQLInListExpr sqlInListExpr = (SQLInListExpr) parentLeft.getLeft();
            map.put(sqlInListExpr.getExpr().toString(), sqlInListExpr);
        }

        if (parentLeft.getRight() instanceof SQLBinaryOpExpr){
            fun((SQLBinaryOpExpr) parentLeft.getRight(), map);
        }else if(parentLeft.getRight() instanceof SQLInListExpr){
            SQLInListExpr sqlInListExpr = (SQLInListExpr) parentLeft.getRight();
            map.put(sqlInListExpr.getExpr().toString(), sqlInListExpr);
        }else if (parentLeft.getRight() instanceof SQLIdentifierExpr){
            map.put(parentLeft.getRight().toString(), parentLeft.getRight());
        }
    }



    public static void parseSelectSql(String sql){


        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement("select * from users");
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;

        List<String> strings = sqlSelectStatement.getSelect().computeSelecteListAlias();

        SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr();



        List<SQLObject> children = sqlStatement.getChildren();


        Pattern compile = Pattern.compile(SELECT_REGEX);
        Matcher matcher = compile.matcher(sql);
        if (matcher.find()){
            String columnStr = matcher.group(1);
            String tableName = matcher.group(2);
            String queryParam = matcher.group(3);

            SQLModel sqlModel = new SQLModel();
            sqlModel.setMethod(SQLModel.Method.SELECT);
            sqlModel.setTableName(tableName);

            List<String> column = new ArrayList<>();
            String[] arr = columnStr.split(",");
            for (String s : arr){
                column.add(s.trim());
            }

        }


    }

    public static Map<String,String> extractTableColumnName(String sql){

        sql = sql.replace("\n","");
        Pattern compile = null;

        if (sql.startsWith("SELECT")){
            compile = Pattern.compile(SELECT_REGEX);
        }else if(sql.startsWith("INSERT INTO")){
            compile = Pattern.compile(INSERT_INTO_REGEX);
        }
        Matcher matcher = compile.matcher(sql);
        if (matcher.find()){
            String tableName = matcher.group(1);
            String columnNameStr = matcher.group(2);
            String valueStr = matcher.group(3);

            System.out.println("tableName: "+tableName);
            System.out.println("columnNameStr: "+columnNameStr);
            System.out.println("valueStr: "+valueStr);
            columnNameStr = columnNameStr.replace("(", "").replace(")", "");
            String[] columnNameArr = columnNameStr.split(",");

            valueStr = valueStr.replace("(", "").replace(")", "");
            String[] valueArr = valueStr.split(",");

            Map<String,String> map = new HashMap<>();
            for (int i=0; i<columnNameArr.length; i++){
                map.put(columnNameArr[i], valueArr[i]);
            }
            return map;
        }
        return Collections.emptyMap();
    }


    @Data
    public static class SQLModel{

        private String tableName;
        private Method method;
        private List<String> columns;
        private Map<String,String> data;

        public enum Method {
            INSERT,
            UPDATE,
            SELECT
        }
    }

}
