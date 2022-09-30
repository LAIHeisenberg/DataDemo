package com.longmai.dbsafe.engine.proxy;


import com.longmai.dbsafe.engine.common.PreparedStatementInformation;
import com.longmai.dbsafe.engine.common.StatementInformation;
import com.longmai.dbsafe.engine.common.Value;
import com.longmai.dbsafe.engine.wrapper.PreparedStatementWrapper;
import com.longmai.dbsafe.engine.wrapper.ResultSetWrapper;
import com.longmai.dbsafe.engine.wrapper.StatementWrapper;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class TargetInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        //加密参数
        encryptParam(o);
        Object result = methodProxy.invokeSuper(o, objects);
        //解密结果
        result = decryptResult(o,objects,result);
        return result;
    }
    private void encryptParam(Object o) throws Exception{
        if (o instanceof PreparedStatementWrapper) {
            Field statementInformationField = o.getClass().getField("statementInformation");
            PreparedStatementInformation preparedStatementInformation = (PreparedStatementInformation) statementInformationField.get(o);
            String sql = preparedStatementInformation.getSqlWithValues();
            if(!Objects.isNull(sql) && sql.toLowerCase().contains("insert") ) {
                Field delegate = o.getClass().getField("delegate");
                PreparedStatement preparedStatement = (PreparedStatement) delegate.get(o);
                Map<Integer, Value> parameterValues = preparedStatementInformation.getParameterValues();
                if (!Objects.isNull(parameterValues) && parameterValues.size()>0) {
                    parameterValues.entrySet().stream().forEach(parameterValue -> {
                        try {
                            if(parameterValue.getKey().equals(2)) {
                                preparedStatement.setObject(parameterValue.getKey() + 1, encrypt(parameterValue.getValue().getValue()));
                            }
                        } catch (SQLException e) {
                            System.out.println(e);
                        }
                    });
                }
            }
        }else if(o instanceof StatementWrapper){
            Field statementInformationField = o.getClass().getField("statementInformation");
            StatementInformation statementInformation = (StatementInformation) statementInformationField.get(o);
            String sql = statementInformation.getSqlWithValues();
            if(!Objects.isNull(sql) && sql.toLowerCase().contains("insert") ) {
                //此处加密参数后替换sql
            }
        }
    }

    private Object decryptResult(Object o,Object[] objects,Object result) {
        //对返回结果进行解密
        if (!Objects.isNull(result) && o instanceof ResultSetWrapper) {
            try {
                if (!Objects.isNull(objects) && "nick_name".equals(objects[0].toString())) {
                    result = decrypt(result);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return result;
    }

    //加密
    private Object encrypt(Object param) {
        Object encryptParam = param;
        if (param instanceof String) {
            String sParam = (String) param;
            byte[] bytes = sParam.getBytes();
            byte[] newBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                newBytes[i] = (byte) (bytes[i] ^ 2);
            }
            encryptParam = new String(newBytes);
        }
        return encryptParam;
    }

    //解密
    private Object decrypt(Object result) {
        Object decryptObject = result;
        if (result instanceof String) {
            //此处解密
            String sResult = (String) result;
            byte[] bytes = (sResult).getBytes();
            byte[] newBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                newBytes[i] = (byte) (bytes[i] ^ 2);
            }
            decryptObject = new String(newBytes);
        }
        return decryptObject;
    }
}
