//package com.longmai.datademo.interceptor;
//
//import com.mysql.cj.MysqlConnection;
//import com.mysql.cj.Query;
//import com.mysql.cj.interceptors.QueryInterceptor;
//import com.mysql.cj.log.Log;
//import com.mysql.cj.protocol.Message;
//import com.mysql.cj.protocol.Resultset;
//import com.mysql.cj.protocol.ServerSession;
//
//import java.util.Properties;
//import java.util.function.Supplier;
//
//public class MaskResultSetScannerInterceptor  implements QueryInterceptor {
//
//    @Override
//    public <M extends Message> M preProcess(M queryPacket) {
//        return QueryInterceptor.super.preProcess(queryPacket);
//    }
//
//    @Override
//    public <M extends Message> M postProcess(M queryPacket, M originalResponsePacket) {
//        return QueryInterceptor.super.postProcess(queryPacket, originalResponsePacket);
//    }
//
//    @Override
//    public QueryInterceptor init(MysqlConnection mysqlConnection, Properties properties, Log log) {
//        return this;
//    }
//
//    @Override
//    public <T extends Resultset> T preProcess(Supplier<String> supplier, Query query) {
//        return null;
//    }
//
//    @Override
//    public boolean executeTopLevelOnly() {
//        return false;
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//
//    @Override
//    public <T extends Resultset> T postProcess(Supplier<String> supplier, Query query, T t, ServerSession serverSession) {
//        return null;
//    }
//}
