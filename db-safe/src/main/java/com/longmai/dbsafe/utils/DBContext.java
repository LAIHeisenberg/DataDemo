package com.longmai.dbsafe.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DBContext {

    public static Map<String,DBInfo> dbInfoContext = new HashMap<>();

    public static final String PROP_HOST = "host"; //$NON-NLS-1$
    public static final String PROP_PORT = "port"; //$NON-NLS-1$
    public static final String PROP_DATABASE = "database"; //$NON-NLS-1$
    public static final String PROP_SERVER = "server"; //$NON-NLS-1$
    public static final String PROP_PARAMS = "params"; //$NON-NLS-1$
    public static final String PROP_FOLDER = "folder"; //$NON-NLS-1$
    public static final String PROP_FILE = "file"; //$NON-NLS-1$
    public static final String PROP_USER = "user"; //$NON-NLS-1$
    public static final String PROP_PASSWORD = "password"; //$NON-NLS-1$
    private final static String MYSQL_JDBCURL_REGEX = "jdbc:mysql://{host}[:{port}]/[{database}][\\?{params}]";
    private final static String ORACLE_JDBCURL_REGEX = "jdbc:oracle:thin:@//{host}[:{port}]/{name}";
    private final static String ORACLE_2_JDBCURL_REGEX = "jdbc:oracle:thin:@{host}[:{port}]:{sid}";
    private final static String MARIADB_JDBCURL_REGEX = "jdbc:mariadb://{host}[:{port}]/[{database}][\\?{params}]";
    private final static String SQLSERVER_JDBCURL_REGEX = "jdbc:sqlserver://{host}[:{port}][;databaseName={database}][;{params}]";
    private final static String DB2_JDBCURL_REGEX = "jdbc:db2://{host}:{port}/{database}[:{params}]";



    public static void main(String[] args){
        saveDBInfo("jdbc:mysql://172.17.2.10:3306/test?useUnicode=true&useSSL=false","laiyz");
    }

    public static void saveDBInfo(String jdbcUrl,String userName){
        Matcher matcher = getPattern(MYSQL_JDBCURL_REGEX).matcher(jdbcUrl);
        if (matcher.matches()){
            DBInfo dbInfo = new DBInfo(matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("database"), "mysql", userName, jdbcUrl, null);
            save(dbInfo);
            return;
        }
        matcher = getPattern(ORACLE_JDBCURL_REGEX).matcher(jdbcUrl);
        if (matcher.matches()){
            DBInfo dbInfo = new DBInfo(matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("database"), "mysql", userName, jdbcUrl, null);
            save(dbInfo);
            return;
        }

        matcher = getPattern(ORACLE_2_JDBCURL_REGEX).matcher(jdbcUrl);
        if (matcher.matches()){
            DBInfo dbInfo = new DBInfo(matcher.group("host"),Integer.parseInt(matcher.group("port")), matcher.group("database"), "mysql", userName, jdbcUrl, null);
            save(dbInfo);
            return;
        }

        matcher = getPattern(SQLSERVER_JDBCURL_REGEX).matcher(jdbcUrl);
        if (matcher.matches()){
            DBInfo dbInfo = new DBInfo(matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("database"), "mysql", userName, jdbcUrl, null);
            save(dbInfo);
            return;
        }

        matcher = getPattern(DB2_JDBCURL_REGEX).matcher(jdbcUrl);
        if (matcher.matches()){
            DBInfo dbInfo = new DBInfo(matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("database"), "mysql", userName, jdbcUrl, null);
            save(dbInfo);
            return;
        }

        matcher = getPattern(MARIADB_JDBCURL_REGEX).matcher(jdbcUrl);
        if (matcher.matches()){
            DBInfo dbInfo = new DBInfo(matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("database"), "mysql", userName, jdbcUrl, null);
            save(dbInfo);
            return;
        }

    }

    public static DBInfo getDBInfo(){
        return dbInfoContext.get("DB_INFO");
    }


    private static void save(DBInfo dbInfo){
        dbInfoContext.put("DB_INFO",dbInfo);
    }

    private static String getPropertyRegex(String property) {
        switch (property) {
            case PROP_FOLDER:
            case PROP_FILE:
            case PROP_PARAMS:
                return ".+?";
            default:
                return "[\\\\w\\\\-_.~]+";
        }
    }

    private static Pattern getPattern(String sampleUrl) {
        String pattern = sampleUrl;
        pattern = replaceAll(pattern, "\\[(.*?)]", m -> "\\\\E(?:\\\\Q" + m.group(1) + "\\\\E)?\\\\Q");
        pattern = replaceAll(pattern, "\\{(.*?)}", m -> "\\\\E(\\?<\\\\Q" + m.group(1) + "\\\\E>" + getPropertyRegex(m.group(1)) + ")\\\\Q");
        pattern = "^\\Q" + pattern + "\\E$";
        return Pattern.compile(pattern);
    }

    private static String replaceAll(String input, String regex, Function<Matcher, String> replacer) {
        final Matcher matcher = Pattern.compile(regex).matcher(input);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacer.apply(matcher));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    public static class DBInfo{
        private String host;
        private Integer port;
        private String dbName;
        private String dbType;
        private String user;
        private String jdbcUrl;
        private String passwd;
    }
}
