/*
 *    Copyright 2009-2014 the original author or authors.
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
package org.apache.ibatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * <h3>执行器</h3>
 * <p>主要是定义了数据处理中的方法。</p>
 * <p>
 * 本接口的实现主要是两类，{@link BaseExecutor} 和 {@link CachingExecutor} 。{@link BaseExecutor} 是符合本接口设计理念并操作数据库实现，
 * {@link CachingExecutor} 却和数据无关，它是从缓存方面实现了类似的功能。
 * </p>
 * <p>
 * MyBatis 底层使用的是 JDBC 技术，参考 {@link java.sql.Statement#executeUpdate Statement.executeUpdate} 方法，
 * 与本接口中的方法“ int {@link #update}(MappedStatement ms, Object parameter) throws SQLException; ”对比，
 * 发现方法名、返回类型有相似之处， 所以猜测此接口参考了 {@link java.sql.Statement} 接口的设计。
 * </p>
 * 
 * @author Clinton Begin
 */
public interface Executor {

	ResultHandler NO_RESULT_HANDLER = null;

	/**
	 * <h3>对数据进行操作</h3>
	 * <p>
	 * 对数据操作，包括插入、删除、修改，返回受影响的行数。
	 * </p>
	 * 
	 * @param ms
	 *            要执行的映射配置文件中 SQL 语句的封装
	 * @param parameter
	 *            外部提供的影响 SQL 语句的参数
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	int update(MappedStatement ms, Object parameter) throws SQLException;

	/**
	 * <h3>对数据进行查询</h3>
	 * <p>
	 * 对数据进行查询操作，并能将数据封装到对象集合中。
	 * </p>
	 * 
	 * @param ms
	 *            要执行的映射配置文件中 SQL 语句的封装
	 * @param parameter
	 *            外部提供的影响 SQL 语句的参数
	 * @param rowBounds
	 *            需要进行内存分页时用到的分页信息
	 * @param resultHandler
	 * @param cacheKey
	 * @param boundSql
	 * @return 封装了数据对象的集合
	 * @throws SQLException
	 */
	<E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql)
			throws SQLException;

	/**
	 * 
	 * @param ms
	 * @param parameter
	 * @param rowBounds
	 * @param resultHandler
	 * @return
	 * @throws SQLException
	 */
	<E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	List<BatchResult> flushStatements() throws SQLException;

	/**
	 * 
	 * @param required
	 * @throws SQLException
	 */
	void commit(boolean required) throws SQLException;

	/**
	 * 
	 * @param required
	 * @throws SQLException
	 */
	void rollback(boolean required) throws SQLException;

	/**
	 * 
	 * @param ms
	 * @param parameterObject
	 * @param rowBounds
	 * @param boundSql
	 * @return
	 */
	CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

	/**
	 * 
	 * @param ms
	 * @param key
	 * @return
	 */
	boolean isCached(MappedStatement ms, CacheKey key);

	/**
	 * 
	 */
	void clearLocalCache();

	/**
	 * 
	 * @param ms
	 * @param resultObject
	 * @param property
	 * @param key
	 * @param targetType
	 */
	void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

	/**
	 * 
	 * @return
	 */
	Transaction getTransaction();

	/**
	 * 
	 * @param forceRollback
	 */
	void close(boolean forceRollback);

	/**
	 * 
	 * @return
	 */
	boolean isClosed();

	/**
	 * 
	 * @param executor
	 */
	void setExecutorWrapper(Executor executor);

}
