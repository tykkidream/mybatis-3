/*
 *    Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session.defaults;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

/**
 * @author Clinton Begin
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

  private final Configuration configuration;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
  * <h3>从数据源创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession() {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
  }

  /**
  * <h3>从数据源创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(boolean autoCommit) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, autoCommit);
  }

  /**
  * <h3>从数据源创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(ExecutorType execType) {
    return openSessionFromDataSource(execType, null, false);
  }

  /**
  * <h3>从数据源创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(TransactionIsolationLevel level) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), level, false);
  }

  /**
  * <h3>从数据源创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
    return openSessionFromDataSource(execType, level, false);
  }

  /**
  * <h3>从数据源创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    return openSessionFromDataSource(execType, null, autoCommit);
  }

  /**
  * <h3>从数据库连接创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(Connection connection) {
    return openSessionFromConnection(configuration.getDefaultExecutorType(), connection);
  }

  /**
  * <h3>从数据库连接创建 SqlSession</h3>
  * 以默认参数创建 SqlSession ，主要是由 {@link #openSessionFromDataSource() } 来创建，参数分别为
  * configuration.getDefaultExecutorType(), null, false 。
  */
  public SqlSession openSession(ExecutorType execType, Connection connection) {
    return openSessionFromConnection(execType, connection);
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  /**
  * <h3>实际创建 SqlSession</h3>
  * 
  * @param execType SQL 语句执行方式，会影响 Executor 。
  * @param level 事务隔离级别。	
  * @param autoCommit 是否自动提交，也就是说是否启用事务。	
  * 
  */
  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
		// Configuration 是全局配置。
		// 1、 从 Configuration 中获取 Environment 。Environment 是全局配置中的数据库连接环境。
		final Environment environment = configuration.getEnvironment();
		// 2、 视 Environment 的情况获取 TransactionFactory 。TransactionFactory 是事务工厂。
		final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
		// 3、 创建 Transaction 。Transaction 是事务。
		tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
		// 3、 创建 Executor 。Transaction 是事务。
		final Executor executor = configuration.newExecutor(tx, execType);
		// 4、 创建 SqlSession 。DefaultSqlSession 是 SqlSession 的默认实现。
		return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private SqlSession openSessionFromConnection(ExecutorType execType, Connection connection) {
    try {
      boolean autoCommit;
      try {
        autoCommit = connection.getAutoCommit();
      } catch (SQLException e) {
        // Failover to true, as most poor drivers
        // or databases won't support transactions
        autoCommit = true;
      }      
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      final Transaction tx = transactionFactory.newTransaction(connection);
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
    if (environment == null || environment.getTransactionFactory() == null) {
      return new ManagedTransactionFactory();
    }
    return environment.getTransactionFactory();
  }

  private void closeTransaction(Transaction tx) {
    if (tx != null) {
      try {
        tx.close();
      } catch (SQLException ignore) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

}
