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
 * <h3>一个 {@link SqlSessionFactory} 的默认实现</h3>
 * @author Clinton Begin
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

	private final Configuration configuration;

	public DefaultSqlSessionFactory(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * <p>实际上是给定某些参数，执行 {@link #openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) } 方法，
	 * 其中参数 execType 默认为内部 {@link Configuration} 对象的  {@link Configuration#getDefaultExecutorType() getDefaultExecutorType()} 的值，
	 *其中参数 level  默认为 null ，参数 autoCommit  默认为 false。</p>
	 */
	public SqlSession openSession() {
		return openSessionFromDataSource(
				configuration.getDefaultExecutorType(), null, false);
	}

	/**
	 * <p>实际上是给定某些参数，执行 {@link #openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) } 方法，
	 * 其中参数 execType 默认为内部 {@link Configuration} 对象的  {@link Configuration#getDefaultExecutorType() getDefaultExecutorType()} 的值，
	 * 参数 level  默认为 null。</p>
	 */
	public SqlSession openSession(boolean autoCommit) {
		return openSessionFromDataSource(
				configuration.getDefaultExecutorType(), null, autoCommit);
	}

	/**
	 * <p>实际上是给定某些参数，执行 {@link #openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) } 方法，
	 * 其中参数 level 默认为 null ，参数 autoCommit  默认为 false。</p>
	 */
	public SqlSession openSession(ExecutorType execType) {
		return openSessionFromDataSource(execType, null, false);
	}

	/**
	 * <p>实际上是给定某些参数，执行 {@link #openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) } 方法，
	 * 其中参数 execType 默认为内部 {@link Configuration} 对象的  {@link Configuration#getDefaultExecutorType() getDefaultExecutorType()} 的值，
	 * 参数 autoCommit  默认为 false。</p>
	 */
	public SqlSession openSession(TransactionIsolationLevel level) {
		return openSessionFromDataSource(
				configuration.getDefaultExecutorType(), level, false);
	}

	/**
	 * <p>实际上是给定某些参数，执行 {@link #openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) } 方法，
	 * 其中参数 autoCommit  默认为 false。</p>
	 */
	public SqlSession openSession(ExecutorType execType,
			TransactionIsolationLevel level) {
		return openSessionFromDataSource(execType, level, false);
	}

	/**
	 * <p>实际上是给定某些参数，执行 {@link #openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) } 方法，
	 * 其中参数 level  默认为 null。</p>
	 */
	public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
		return openSessionFromDataSource(execType, null, autoCommit);
	}

	/**
	 * <p>实际上是给定某些参数，执行了 {@link #openSessionFromConnection(ExecutorType execType, Connection connection) } 方法，
	 * 其中参数 execType 默认为内部 {@link Configuration} 对象的  {@link Configuration#getDefaultExecutorType() getDefaultExecutorType()} 的值。</p>
	 */
	public SqlSession openSession(Connection connection) {
		return openSessionFromConnection(
				configuration.getDefaultExecutorType(), connection);
	}

	/**
	 * <p>实际上是传递所有参数，执行了{@link #openSessionFromConnection(ExecutorType execType, boolean autoCommit) } 方法。</p>
	 */
	public SqlSession openSession(ExecutorType execType, Connection connection) {
		return openSessionFromConnection(execType, connection);
	}

	/**
	 * 获取内部的 {@link Configuration} 对象。
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * <h3>从数据源创建 SqlSession</h3>
	 * <p>使用的数据库连接是从 {@link Configuration}.{@link Configuration#getEnvironment() getEnvironment()}.{@link Environment#getDataSource() getDataSource()} 获取。</p>
	 * 
	 * @param execType
	 *            SQL 语句执行方式，会影响 Executor 。
	 * @param level
	 *            事务隔离级别。
	 * @param autoCommit
	 *            是否自动提交，也就是说是否启用事务。
	 * 
	 */
	private SqlSession openSessionFromDataSource(ExecutorType execType,
			TransactionIsolationLevel level, boolean autoCommit) {
		Transaction tx = null;
		try {
			// Configuration 是全局配置。
			// 1、 从 Configuration 中获取 Environment 。Environment 是全局配置中的数据库连接环境。
			final Environment environment = configuration.getEnvironment();
			// 2、 视 Environment 的情况获取 TransactionFactory 。TransactionFactory 是事务工厂。
			final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
			// 3、 创建 Transaction 。Transaction 是事务。
			tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
			// 4、 创建 Executor 。Executor 是 MyBatis 执行器。
			final Executor executor = configuration.newExecutor(tx, execType);
			// 5、 创建 SqlSession 。DefaultSqlSession 是 SqlSession 的默认实现。
			return new DefaultSqlSession(configuration, executor, autoCommit);
		} catch (Exception e) {
			closeTransaction(tx); // may have fetched a connection so lets call
									// close()
			throw ExceptionFactory.wrapException(
					"Error opening session.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	/**
	 * <h3>从数据连接创建 SqlSession</h3>
	 * <p>使用的数据库连接是参数指定的，不是从 {@link Configuration} 获取的。是否使用事务由 {@link Connection#getAutoCommit()} 指定，
	 * 相关事务的细节由内部 {@link Configuration} 设定。</p>
	 * 
	 * @param execType
	 * @param connection
	 * @return
	 */
	private SqlSession openSessionFromConnection(ExecutorType execType,
			Connection connection) {
		try {
			boolean autoCommit;
			try {
				autoCommit = connection.getAutoCommit();
			} catch (SQLException e) {
				// Failover to true, as most poor drivers
				// or databases won't support transactions
				autoCommit = true;
			}
			// 1、 从 Configuration 中获取 Environment 。Environment 是全局配置中的数据库连接环境。
			final Environment environment = configuration.getEnvironment();
			// 2、 视 Environment 的情况获取 TransactionFactory 。TransactionFactory 是事务工厂。
			final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
			// 3、 创建 Transaction 。Transaction 是事务。
			final Transaction tx = transactionFactory.newTransaction(connection);
			// 4、 创建 Executor 。Executor 是 MyBatis 执行器。
			final Executor executor = configuration.newExecutor(tx, execType);
			// 5、 创建 SqlSession 。DefaultSqlSession 是 SqlSession 的默认实现。
			return new DefaultSqlSession(configuration, executor, autoCommit);
		} catch (Exception e) {
			throw ExceptionFactory.wrapException(
					"Error opening session.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	private TransactionFactory getTransactionFactoryFromEnvironment(
			Environment environment) {
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
