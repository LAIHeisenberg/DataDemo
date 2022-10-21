package com.longmai.dbsafe.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBSQLUtils {

    private static final String INSERT_INTO_REGEX = "INSERT\\s+INTO\\s+([a-zA-Z_.]+)\\s*\\([a-zA-Z_,\\s]+\\)";
    private static final String SELECT_REGEX = "SELECT.*FROM\\s+([a-zA-Z\\._]+)\\s+['WHERE'|'ORDERY BY'|'LIMIT']?";
    private static final String UPDATE_REGEX = "UPDATE\\s+([a-zA-Z_.]+)\\s*";

    public static void main(String[] args){
        String sql1 = "insert into tbkl.ddm_user(user_name, nick_name) values(1,12)";
        String sql2 = "UPDATE dd.ddm_user set a=1 where id= 10";
        String sql = "SELECT id, user_name, nick_name, email, phone\n" +
                "\t, gender, password, auth_method, admin_flag, dn\n" +
                "\t, cert, enabled, create_time, update_time \n" +
                "FROM ddm_user\n" +
                "WHERE user_name = 'admin' and create_time in (1,5) and enabled = true or auth_method > 2  and phone = 1234123512";

        System.out.println(getTableName(sql2));
    }

    public static String getTableName(String sql){
        sql = sql.replace("\n"," ").replace("\t","");
        Pattern compile = null;
        if (sql.startsWith("SELECT")){
            compile = Pattern.compile(SELECT_REGEX);
        }else if(sql.startsWith("INSERT INTO")){
            compile = Pattern.compile(INSERT_INTO_REGEX);
        }else if (sql.startsWith("UPDATE")){
            compile = Pattern.compile(UPDATE_REGEX);
        }
        Matcher matcher = compile.matcher(sql);
        if (matcher.find()){
            String[] arr = matcher.group(1).trim().split("\\.");
            return arr[arr.length-1];
        }
        return null;
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
