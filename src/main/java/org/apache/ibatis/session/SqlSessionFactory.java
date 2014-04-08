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
package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * <h3>创建 SqlSession 的接口</h3>
 * <p>
 * 创建 {@link SqlSession} ，由于它的创建依赖  {@link Configuration} 中的配置信息，所以这个接口的实现类中应该包含一个  {@link Configuration} 对象。
 * </p>
 * <p>
 * 而创建过程复杂繁索，为了简便开发 MyBatis 使用了工厂模式，因此定义了这个接口工厂，可参考默认的实现 
 * {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory DefaultSqlSessionFactory} 。
 * </p>
 * <p>
 * 另外此接口只使用并不创建  {@link Configuration} ，所以 MyBatis 还提供了一个的工厂来生产此接口实例，参考 {@link org.apache.ibatis.session.SqlSessionFactoryBuilder SqlSessionFactoryBuilder}。
 * </p>
 * 
 * <hr>
 * <p>
 * Creates an {@link SqlSession} out of a connection or a DataSource.
 * </p>
 * <p>
 * 创建 {@link SqlSession} ，根据一个 {@link Connection} 或 {@link javax.sql.DataSource
 * DataSource} 创建 。
 * </p>
 * 
 * @author Clinton Begin
 * @see org.apache.ibatis.session.SqlSessionFactoryBuilder
 * @see org.apache.ibatis.session.defaults.DefaultSqlSessionFactory
 */
public interface SqlSessionFactory {

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>使用默认参数获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession() DefaultSqlSessionFactory.openSession()}</p>
	 * @return  {@link SqlSession}
	 */
	SqlSession openSession();

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据是否支持自动提交获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession(boolean) DefaultSqlSessionFactory.openSession(boolean)}</p>
	 * @param autoCommit 是否能自动提交， Boolean 类型， true 为支持， false 为不支持。
	 * @return {@link SqlSession}
	 */
	SqlSession openSession(boolean autoCommit);

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据一个数据库连接获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession(Connection) DefaultSqlSessionFactory.openSession(Connection)}</p>
	 * @param connection 数据库连接， {@link Connection} 类型。
	 * @return {@link SqlSession} 
	 */
	SqlSession openSession(Connection connection);

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据数据隔离级别 {@link TransactionIsolationLevel} 获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession(TransactionIsolationLevel) DefaultSqlSessionFactory.openSession(TransactionIsolationLevel)}</p>
	 * @param level 数据隔离级别。
	 * @return {@link SqlSession} 
	 */
	SqlSession openSession(TransactionIsolationLevel level);

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据执行器的执行方式 {@link ExecutorType} 获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession() DefaultSqlSessionFactory.openSession(ExecutorType)}</p>
	 * @param execType 执行器的执行。
	 * @return  {@link SqlSession}
	 */
	SqlSession openSession(ExecutorType execType);

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据执行器的执行方式 {@link ExecutorType} 和是否支持自动提交获取一个 {@link SqlSession}，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession() DefaultSqlSessionFactory.openSession(ExecutorType, boolean)}</p>
	 * @param execType 执行器的执行。
	 * @param autoCommit 是否能自动提交， Boolean 类型， true 为支持， false 为不支持。
	 * @return  {@link SqlSession}
	 */
	SqlSession openSession(ExecutorType execType, boolean autoCommit);

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据执行器的执行方式 {@link ExecutorType} 和数据隔离级别 {@link TransactionIsolationLevel} 获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession() DefaultSqlSessionFactory.openSession(ExecutorType, TransactionIsolationLevel)}</p>
	 * @param execType 执行器的执行。
	 * @param level 数据隔离级别，  {@link TransactionIsolationLevel} 类型。
	 * @return {@link SqlSession} 
	 */
	SqlSession openSession(ExecutorType execType,
			TransactionIsolationLevel level);

	/**
	 * <h3>创建 SqlSession</h3>
	 * <p>根据执行器的执行方式 {@link ExecutorType} 和数据库连接获取一个 {@link SqlSession} ，参考其默认的实现
	 *  {@link org.apache.ibatis.session.defaults.DefaultSqlSessionFactory#openSession() DefaultSqlSessionFactory.openSession(ExecutorType, Connection)}</p>
	 * @param execType 执行器的执行。
	 * @param connection 数据库连接， {@link Connection} 类型。
	 * @return {@link SqlSession} 
	 */
	SqlSession openSession(ExecutorType execType, Connection connection);

	/**
	 * <h3>得到 Configuration</h3>
	 * <p>获取一个 {@link Configuration} 。创建 {@link SqlSession} 时要用到某些配置应该是从这个对象上得到的。</p>
	 * @return {@link Configuration}
	 */
	Configuration getConfiguration();

}
