package com.longmai.dbsafe.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import lombok.Data;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBSQLUtils {

    private static final String INSERT_INTO_REGEX = "INSERT\\s+INTO\\s+([a-zA-Z_.]+)\\s*\\([a-zA-Z_,\\s]+\\)";
    private static final String SELECT_REGEX = "SELECT.*FROM\\s+([a-zA-Z\\._]+)\\s+['WHERE'|'ORDERY BY'|'LIMIT']?";
    private static final String UPDATE_REGEX = "UPDATE\\s+([a-zA-Z_.]+)\\s*";
    private static final String DELETE_REGEX = "DELETE FROM\\s+([a-zA-Z_.]+)\\s*";

    public static void main(String[] args){
        String sql1 = "insert into tbkl.ddm_user(user_name, nick_name) values(1,12)";
        String sql2 = "UPDATE dd.ddm_user set a=1 where id= 10";
        String sql = "SELECT id, user_name, nick_name, email, phone\n" +
                "\t, gender, password, auth_method, admin_flag, dn\n" +
                "\t, cert, enabled, create_time, update_time \n" +
                "FROM ddm_user\n" +
                "WHERE user_name = 'admin' and create_time in (1,5) and enabled = true or auth_method > 2  and phone = 1234123512";
        String testSql = "SELECT quartzjob0_.job_id AS job_id1_14_, quartzjob0_.create_by AS create_b2_14_, quartzjob0_.create_time AS create_t3_14_, quartzjob0_.update_by AS update_b4_14_, quartzjob0_.update_time AS update_t5_14_\n" +
                "\t, quartzjob0_.bean_name AS bean_nam6_14_, quartzjob0_.cron_expression AS cron_exp7_14_, quartzjob0_.description AS descript8_14_, quartzjob0_.email AS email9_14_, quartzjob0_.is_pause AS is_paus10_14_\n" +
                "\t, quartzjob0_.job_name AS job_nam11_14_, quartzjob0_.method_name AS method_12_14_, quartzjob0_.params AS params13_14_, quartzjob0_.pause_after_failure AS pause_a14_14_, quartzjob0_.person_in_charge AS person_15_14_\n" +
                "\t, quartzjob0_.sub_task AS sub_tas16_14_\n" +
                "FROM sys_quartz_job quartzjob0_\n" +
                "WHERE 1 = 1\n" +
                "ORDER BY quartzjob0_.job_id DESC\n" +
                "LIMIT 10";

        String sql3 = "SELECT r.*\n" +
                "FROM sys_role r, sys_users_roles u\n" +
                "WHERE r.role_id = u.role_id\n" +
                "\tAND u.user_id = 1";

        Map<String,String> aliasMapColumnName = new LinkedHashMap<>();

        SQLSelectStatement sqlStatement = (SQLSelectStatement)SQLUtils.parseSingleMysqlStatement(sql3);
        SQLSelect select = sqlStatement.getSelect();
        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) select.getQuery();
        List<SQLSelectItem> selectList = query.getSelectList();
        for (SQLSelectItem selectItem : selectList){
            String alias = selectItem.getAlias();
            if (Objects.nonNull(alias)){
                SQLPropertyExpr expr = (SQLPropertyExpr)selectItem.getExpr();
                aliasMapColumnName.put(alias, expr.getName());
            }
        }

        System.out.println(getTableName(sql2));
    }

    public static String getTableName(String sql){
        sql = sql.replace("\n"," ").replace("\t","").toUpperCase();
        Pattern compile = null;
        if (sql.startsWith("SELECT")){
            compile = Pattern.compile(SELECT_REGEX);
        }else if(sql.startsWith("INSERT INTO")){
            compile = Pattern.compile(INSERT_INTO_REGEX);
        }else if (sql.startsWith("UPDATE")){
            compile = Pattern.compile(UPDATE_REGEX);
        }else if (sql.startsWith("DELETE")){
            compile = Pattern.compile(DELETE_REGEX);
        }
        Matcher matcher = compile.matcher(sql);
        if (matcher.find()){
            String[] arr = matcher.group(1).trim().split("\\.");
            return arr[arr.length-1];
        }
        return null;
    }

    public static void fun(SQLExpr sqlExpr, Map<String, SQLExpr> map){
        if (sqlExpr instanceof SQLBinaryOpExpr){
            fun((SQLBinaryOpExpr) sqlExpr, map);
        }else if (sqlExpr instanceof SQLInListExpr){
            map.put(((SQLInListExpr) sqlExpr).getExpr().toString(), sqlExpr);
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
