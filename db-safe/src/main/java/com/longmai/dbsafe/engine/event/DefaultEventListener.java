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


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.longmai.datakeeper.rest.dto.DBEncryptDto;
import com.longmai.dbsafe.encrypt.DBEncryptFactory;
import com.longmai.dbsafe.encrypt.IEncrypt;
import com.longmai.dbsafe.engine.common.CallableStatementInformation;
import com.longmai.dbsafe.engine.common.PreparedStatementInformation;
import com.longmai.dbsafe.engine.common.ResultSetInformation;
import com.longmai.dbsafe.engine.common.StatementInformation;
import com.longmai.dbsafe.utils.DBEncryptContext;
import com.longmai.dbsafe.utils.DBSQLUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String tableName= DBSQLUtils.getTableName(sqlStatement.toString());
    if (sqlStatement instanceof SQLInsertStatement){
      SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) sqlStatement;
      List<SQLExpr> columns = sqlInsertStatement.getColumns();
      SQLInsertStatement.ValuesClause valuesClause = sqlInsertStatement.getValues();
      Map<String, SQLExpr> map = new HashMap<>();
      for (int i=0; i<columns.size(); i++){
        map.put(columns.get(i).toString(), valuesClause.getValues().get(i));
      }
      //加密操作
      List<DBEncryptDto.EncryptColumnDto> encryptColumnDtos = DBEncryptContext.listColumn(tableName);
      if (encryptColumnDtos.size() > 0){
        encryptColumnDtos.forEach(new Consumer<DBEncryptDto.EncryptColumnDto>() {
          @Override
          public void accept(DBEncryptDto.EncryptColumnDto encryptColumnDto) {
            String columnName = encryptColumnDto.getColumnName();
            SQLExpr sqlExpr = map.get(columnName);
            if (sqlExpr instanceof SQLCharExpr){
              SQLCharExpr sqlCharExpr = (SQLCharExpr) sqlExpr;
              String plainText = sqlCharExpr.getText();
              String algorithm = encryptColumnDto.getAlgorithm();
              IEncrypt encryptInstance = DBEncryptFactory.getEncryptInstance(algorithm);
              try {
                byte[] encryptBytes = encryptInstance.encrypt(encryptColumnDto.getSecretKey().getBytes("utf-8"), plainText.getBytes("utf-8"));
                sqlCharExpr.setText(new String(encryptBytes,"utf-8"));
                sqlInsertStatement.setValues(valuesClause);
              }catch (Exception e){
                e.printStackTrace();
              }
            }
          }
        });
      }
    }else if (sqlStatement instanceof SQLSelectStatement){
      SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
      SQLSelectQueryBlock queryBlock = sqlSelectStatement.getSelect().getQueryBlock();
      Map<String, SQLExpr> map = new HashMap<>();
      DBSQLUtils.fun(queryBlock.getWhere(), map);
      List<DBEncryptDto.EncryptColumnDto> encryptColumnDtos = DBEncryptContext.listColumn(tableName);
      if (encryptColumnDtos.size() > 0){
        encryptColumnDtos.forEach(new Consumer<DBEncryptDto.EncryptColumnDto>() {
          @Override
          public void accept(DBEncryptDto.EncryptColumnDto encryptColumnDto) {
            String columnName = encryptColumnDto.getColumnName();
            SQLExpr sqlExpr = map.get(columnName);
            if (sqlExpr instanceof SQLBinaryOpExpr){
              SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr)sqlExpr;
              SQLCharExpr sqlCharExpr = (SQLCharExpr) sqlBinaryOpExpr.getRight();
              SQLCharExpr clone = sqlCharExpr.clone();
              String plainText = sqlCharExpr.getText();
              String algorithm = encryptColumnDto.getAlgorithm();
              IEncrypt encryptInstance = DBEncryptFactory.getEncryptInstance(algorithm);
              try {
                byte[] encryptBytes = encryptInstance.encrypt(encryptColumnDto.getSecretKey().getBytes("utf-8"), plainText.getBytes("utf-8"));
                clone.setText(new String(encryptBytes,"utf-8"));
                System.out.println("plainText: "+plainText + "encryptText: "+new String(encryptBytes,"utf-8"));
                sqlBinaryOpExpr.replace(sqlCharExpr, clone);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        });
      }
    }

    statementInformation.setStatementQuery(SQLUtils.toSQLString(sqlStatement));
  }

  @Override
  public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onBeforeExecuteBatch(StatementInformation statementInformation) {
    PreparedStatementInformation preparedStatementInformation = new PreparedStatementInformation(statementInformation.getConnectionInformation(), statementInformation.getSql());
    onBeforeExecute(preparedStatementInformation);
  }

  @Override
  public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
    onBeforeExecute(statementInformation);
  }

  @Override
  public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }


  @Override
  public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
    onBeforeExecute(statementInformation);
  }

  @Override
  public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);

  }

  @Override
  public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {

  }

  @Override
  public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    statementInformation.incrementTimeElapsed(timeElapsedNanos);
  }

  @Override
  public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {

  }

  @Override
  public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
    resultSetInformation.getStatementInformation().incrementTimeElapsed(timeElapsedNanos);
    if (hasNext) {
      resultSetInformation.incrementCurrRow();
    }
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
