package com.longmai.dbsafe.engine.proxy;


import com.longmai.dbsafe.engine.common.PreparedStatementInformation;
import com.longmai.dbsafe.engine.common.Value;
import com.longmai.dbsafe.engine.wrapper.PreparedStatementWrapper;
import com.longmai.dbsafe.engine.wrapper.ResultSetWrapper;
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

        if (o instanceof PreparedStatementWrapper && method.getName().contains("execute")) {
            Field statementInformation = o.getClass().getField("statementInformation");
            Field delegate = o.getClass().getField("delegate");

            PreparedStatementInformation preparedStatementInformation = (PreparedStatementInformation) statementInformation.get(o);
            String sql = preparedStatementInformation.getSqlWithValues();
            if(!Objects.isNull(sql) && sql.toLowerCase().contains("insert") ) {
                PreparedStatement preparedStatement = (PreparedStatement) delegate.get(o);
                Map<Integer, Value> parameterValues = preparedStatementInformation.getParameterValues();
                if (!Objects.isNull(parameterValues) && parameterValues.size()>0) {
                    parameterValues.entrySet().stream().forEach(parameterValue -> {
                        try {
                            if(parameterValue.getKey().equals(2)) {
                                preparedStatement.setObject(parameterValue.getKey() + 1, encrypt(parameterValue.getValue().getValue()));
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }

        }

        Object result = methodProxy.invokeSuper(o, objects);

        //对返回结果进行解密
        if (!Objects.isNull(result) && o instanceof ResultSetWrapper) {
            try {
                if (!Objects.isNull(objects) && objects[0] instanceof String && "nick_name".equals(objects[0].toString())) {
                    result = decrypt(result);
                }
            } catch (Exception e) {
                System.out.println("异常");
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
