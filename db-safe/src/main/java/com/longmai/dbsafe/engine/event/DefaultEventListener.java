/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2020 P6Spy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.longmai.dbsafe.engine.event;


import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.longmai.dbsafe.engine.common.*;
import com.longmai.dbsafe.engine.logging.Category;
import com.longmai.dbsafe.utils.DBSQLUtils;
import net.sf.cglib.core.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;

/**
 * This implementation of {@link JdbcEventListener} must always be applied as the first listener.
 * It populates the information objects {@link StatementInformation}, {@link PreparedStatementInformation},
 * {@link com.p6spy.engine.common.CallableStatementInformation} and {@link ResultSetInformation}
 */
public class DefaultEventListener extends JdbcEventListener {

  public static final DefaultEventListener INSTANCE = new DefaultEventListener();

  private DefaultEventListener() {
  }

  @Override
  public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
  }

  /**
   * This callback method is executed before any of the {@link PreparedStatement#execute()} methods are invoked.
   *
   * @param statementInformation The meta information about the {@link Statement} being invoked
   */
  @Override
  public void onBeforeExecute(PreparedStatementInformation statementInformation) {
    SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(statementInformation.getSqlWithValues());
    if (sqlStatement instanceof SQLInsertStatement){
      SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) sqlStatement;
      SQLName tableName = sqlInsertStatement.getTableName();
      DbType dbType = sqlInsertStatement.getDbType();
      List<SQLExpr> columns = sqlInsertStatement.getColumns();
      SQLInsertStatement.ValuesClause valuesClause = sqlInsertStatement.getValues();
      Map<String, SQLExpr> map = new HashMap<>();
      for (int i=0; i<columns.size(); i++){
        map.put(((SQLIdentifierExpr)columns.get(i)).getName(), valuesClause.getValues().get(i));
      }

      //加密操作
      List<String> encodeFieldList =  Arrays.asList("phone");
      encodeFieldList.forEach(new Consumer<String>() {
        @Override
        public void accept(String s) {
          SQLCharExpr sqlExpr = (SQLCharExpr)map.get(s);
          sqlExpr.setText("1888888899");
          valuesClause.replace(sqlExpr, sqlExpr);
          sqlInsertStatement.setValues(valuesClause);
        }
      });

    }else if (sqlStatement instanceof SQLSelectStatement){
      SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
      SQLSelectQueryBlock queryBlock = sqlSelectStatement.getSelect().getQueryBlock();
      Map<String, SQLExpr> map = new HashMap<>();
      DBSQLUtils.fun((SQLBinaryOpExpr)queryBlock.getWhere(), map);

      //加密操作
      List<String> encodeFieldList =  Arrays.asList("user_name");
      encodeFieldList.stream().forEach(new Consumer<String>() {
        @Override
        public void accept(String s) {
          SQLExpr sqlExpr = map.get(s);
          if (sqlExpr instanceof SQLBinaryOpExpr){
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr)sqlExpr;
            SQLCharExpr sqlCharExpr = (SQLCharExpr) sqlBinaryOpExpr.getRight();
            SQLCharExpr clone = sqlCharExpr.clone();
            String text = sqlCharExpr.getText();
//            clone.setText(text+"aa");
//            sqlBinaryOpExpr.replace(sqlCharExpr, clone);
          }
        }
      });
    }

    statementInformation.setStatementQuery(SQLUtils.toSQLString(sqlStatement));
    System.out.println("onBeforeExecute: "+statementInformation.getSql());
  }

  @Override
  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);

    System.out.println("onAfterExecute1: "+statementInformation.getSqlWithValues());
  }

  @Override
  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);

    System.out.println("onAfterExecute2: "+statementInformation.getSql());
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
    System.out.println("onBeforeExecuteUpdate: "+statementInformation.getSql());
  }

  @Override
  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
    System.out.println("onAfterExecuteUpdate: "+statementInformation.getSql());
  }


  @Override
  public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
    //todo加密查询字段
    System.out.println("onBeforeExecuteQuery: "+statementInformation.getSql());
  }

  @Override
  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
    System.out.println("onAfterExecuteQuery1: "+statementInformation.getSql());
  }

  @Override
  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);

    System.out.println("onAfterExecuteQuery1: "+sql);
  }

  @Override
  public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
    System.out.println("onBeforeResultSetNext..");
  }

  @Override
  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
    System.out.println("onAfterGetResultSet");
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {
    System.out.println("onAfterResultSetGet");
  }

  @Override
  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
    resultSetInformation.getStatementInformation().incrementTimeElapsed(timeElapsedNanos);

    System.out.println("onAfterResultSetNext");
    if (hasNext) {
      resultSetInformation.incrementCurrRow();
    }
    ResultSet resultSet = resultSetInformation.getResultSet();
    //解密
    List<String> list = Arrays.asList("user_name","nick_name");
    list.forEach(new Consumer<String>() {
      @Override
      public void accept(String s) {
//
//        try {
//          String value = resultSet.getString(s);
//          resultSet.updateString(s, "$"+value+"%");
//        } catch (SQLException ex) {
//          ex.printStackTrace();
//        }

      }
    });

  }

  @Override
  public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value, SQLException e) {
    statementInformation.setParameterValue(parameterName, value);
  }

  @Override
  public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value, SQLException e) {
    statementInformation.setParameterValue(parameterIndex, value);
  }

}
