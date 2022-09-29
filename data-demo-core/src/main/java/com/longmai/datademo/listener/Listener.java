package com.longmai.datademo.listener;


import com.longmai.dbsafe.engine.common.*;
import com.longmai.dbsafe.engine.event.JdbcEventListener;

import java.sql.SQLException;

public class Listener extends JdbcEventListener {
    @Override
    public void onBeforeAddBatch(PreparedStatementInformation statementInformation) {
        super.onBeforeAddBatch(statementInformation);
    }

    @Override
    public void onAfterAddBatch(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        super.onAfterAddBatch(statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onBeforeAddBatch(StatementInformation statementInformation, String sql) {
        super.onBeforeAddBatch(statementInformation, sql);
    }

    @Override
    public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
        super.onAfterAddBatch(statementInformation, timeElapsedNanos, sql, e);
    }

    @Override
    public void onBeforeExecute(PreparedStatementInformation statementInformation) {
        super.onBeforeExecute(statementInformation);
    }

    @Override
    public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        super.onAfterExecute(statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onBeforeExecute(StatementInformation statementInformation, String sql) {
        super.onBeforeExecute(statementInformation, sql);
    }

    @Override
    public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
        super.onAfterExecute(statementInformation, timeElapsedNanos, sql, e);
    }

    @Override
    public void onBeforeExecuteBatch(StatementInformation statementInformation) {
        super.onBeforeExecuteBatch(statementInformation);
    }

    @Override
    public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
        super.onAfterExecuteBatch(statementInformation, timeElapsedNanos, updateCounts, e);
    }

    @Override
    public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
        super.onBeforeExecuteUpdate(statementInformation);
    }

    @Override
    public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
        super.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, rowCount, e);
    }

    @Override
    public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
        super.onBeforeExecuteUpdate(statementInformation, sql);
    }

    @Override
    public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
        super.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, sql, rowCount, e);
    }

    @Override
    public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
        super.onBeforeExecuteQuery(statementInformation);
    }

    @Override
    public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        super.onAfterExecuteQuery(statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
        super.onBeforeExecuteQuery(statementInformation, sql);
    }

    @Override
    public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
        super.onAfterExecuteQuery(statementInformation, timeElapsedNanos, sql, e);
    }

    @Override
    public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value, SQLException e) {
        super.onAfterPreparedStatementSet(statementInformation, parameterIndex, value, e);
    }

    @Override
    public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value, SQLException e) {
        super.onAfterCallableStatementSet(statementInformation, parameterName, value, e);
    }

    @Override
    public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        super.onAfterGetResultSet(statementInformation, timeElapsedNanos, e);
    }

    @Override
    public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
        super.onBeforeResultSetNext(resultSetInformation);
    }

    @Override
    public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
        super.onAfterResultSetNext(resultSetInformation, timeElapsedNanos, hasNext, e);
    }

    @Override
    public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
        super.onAfterResultSetClose(resultSetInformation, e);
    }

    @Override
    public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value, SQLException e) {
        super.onAfterResultSetGet(resultSetInformation, columnLabel, value, e);
    }

    @Override
    public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {
        super.onAfterResultSetGet(resultSetInformation, columnIndex, value, e);
    }

    @Override
    public void onBeforeCommit(ConnectionInformation connectionInformation) {
        super.onBeforeCommit(connectionInformation);
    }

    @Override
    public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
        super.onAfterCommit(connectionInformation, timeElapsedNanos, e);
    }

    @Override
    public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
        super.onAfterConnectionClose(connectionInformation, e);
    }

    @Override
    public void onBeforeRollback(ConnectionInformation connectionInformation) {
        super.onBeforeRollback(connectionInformation);
    }

    @Override
    public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
        super.onAfterRollback(connectionInformation, timeElapsedNanos, e);
    }
}
