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
package org.apache.ibatis.session.defaults;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.result.DefaultMapResultHandler;
import org.apache.ibatis.executor.result.DefaultResultContext;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

/**
 * @author Clinton Begin
 */
public class DefaultSqlSession implements SqlSession {

	private Configuration configuration;
	
	/**
	 * <p>对于当前 DefaultSqlSession 来说，它是实际执行数据库操作的对象。所有的 select 、 selectOne 、selectList 、selectMap 、
	 * insert 、 delete 、update 、commit 、rollback 和重载方法实际上都是使用它执行的。</p>
	 * <p>所以，在这里的发现，真正干活的是 Executor 。注意仅是在这里而已，再往 Executor 里阅读源代码又能发现新的不同。</p>
	 */
	private Executor executor;

	private boolean autoCommit;
	private boolean dirty;

	public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
		this.configuration = configuration;
		this.executor = executor;
		this.dirty = false;
		this.autoCommit = autoCommit;
	}

	public DefaultSqlSession(Configuration configuration, Executor executor) {
		this(configuration, executor, false);
	}

	public <T> T selectOne(String statement) {
		return this.<T> selectOne(statement, null);
	}

	public <T> T selectOne(String statement, Object parameter) {
		// Popular vote was to return null on 0 results and throw exception on
		// too many.
		List<T> list = this.<T> selectList(statement, parameter);
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
		} else {
			return null;
		}
	}

	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return this.selectMap(statement, null, mapKey, RowBounds.DEFAULT);
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		return this.selectMap(statement, parameter, mapKey, RowBounds.DEFAULT);
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		final List<?> list = selectList(statement, parameter, rowBounds);
		final DefaultMapResultHandler<K, V> mapResultHandler = new DefaultMapResultHandler<K, V>(mapKey, configuration.getObjectFactory(),
				configuration.getObjectWrapperFactory());
		final DefaultResultContext context = new DefaultResultContext();
		for (Object o : list) {
			context.nextResultObject(o);
			mapResultHandler.handleResult(context);
		}
		Map<K, V> selectedMap = mapResultHandler.getMappedResults();
		return selectedMap;
	}

	public <E> List<E> selectList(String statement) {
		return this.selectList(statement, null);
	}

	public <E> List<E> selectList(String statement, Object parameter) {
		return this.selectList(statement, parameter, RowBounds.DEFAULT);
	}

	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		try {
			// statement 是 MyBatis 映射语句的 ID ，根据它找到对应的“映射语句对象”。
			MappedStatement ms = configuration.getMappedStatement(statement);
			// 执行查询。
			// @ 1、传递要被执行的映射语句对象。
			// @ 2、特别地按照一些规则把 parameter 处理一下，使它能被用于映射语句中解析的参数。
			// @ 3、MyBatis 自身的内存分页参数。
			// @ 4、
			List<E> result = executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
			return result;
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public void select(String statement, Object parameter, ResultHandler handler) {
		select(statement, parameter, RowBounds.DEFAULT, handler);
	}

	public void select(String statement, ResultHandler handler) {
		select(statement, null, RowBounds.DEFAULT, handler);
	}

	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
		try {
			MappedStatement ms = configuration.getMappedStatement(statement);
			executor.query(ms, wrapCollection(parameter), rowBounds, handler);
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public int insert(String statement) {
		return insert(statement, null);
	}

	public int insert(String statement, Object parameter) {
		return update(statement, parameter);
	}

	public int update(String statement) {
		return update(statement, null);
	}

	public int update(String statement, Object parameter) {
		try {
			dirty = true;
			MappedStatement ms = configuration.getMappedStatement(statement);
			return executor.update(ms, wrapCollection(parameter));
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public int delete(String statement) {
		return update(statement, null);
	}

	public int delete(String statement, Object parameter) {
		return update(statement, parameter);
	}

	public void commit() {
		commit(false);
	}

	public void commit(boolean force) {
		try {
			executor.commit(isCommitOrRollbackRequired(force));
			dirty = false;
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public void rollback() {
		rollback(false);
	}

	public void rollback(boolean force) {
		try {
			executor.rollback(isCommitOrRollbackRequired(force));
			dirty = false;
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error rolling back transaction.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public List<BatchResult> flushStatements() {
		try {
			return executor.flushStatements();
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error flushing statements.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public void close() {
		try {
			executor.close(isCommitOrRollbackRequired(false));
			dirty = false;
		} finally {
			ErrorContext.instance().reset();
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public <T> T getMapper(Class<T> type) {
		return configuration.<T> getMapper(type, this);
	}

	public Connection getConnection() {
		try {
			return executor.getTransaction().getConnection();
		} catch (SQLException e) {
			throw ExceptionFactory.wrapException("Error getting a new connection.  Cause: " + e, e);
		}
	}

	public void clearCache() {
		executor.clearLocalCache();
	}

	private boolean isCommitOrRollbackRequired(boolean force) {
		return (!autoCommit && dirty) || force;
	}

	/**
	 * <h3>根据（原）参数获得将被传递到映射语句中的参数</h3>
	 * <p>
	 * 如果用户传递过来的参数是 List 类型、 数组类型，就把它们包装在一个特别的 Map 中，各自的 Key 分别是 “list” 、“array”。
	 * 否则，返回原参数本身。
	 * </p>
	 * @param object
	 * @return
	 */
	private Object wrapCollection(final Object object) {
		if (object instanceof List) {
			// 和 HashMap 的区别只有一点，大部分场合用不上，这里也没有用上。
			// 这个对象就是最终在映射文件里映射语句中的参数，暂且称为语句参数。
			StrictMap<Object> map = new StrictMap<Object>();
			// List 类型的参数，都是以 Key 为 “ list ”放在语句参数中的。
			map.put("list", object);
			return map;
		} else if (object != null && object.getClass().isArray()) {
			// 和 HashMap 的区别只有一点，大部分场合用不上，这里也没有用上。
			// 这个对象就是最终在映射文件里映射语句中的参数，暂且称为语句参数。
			StrictMap<Object> map = new StrictMap<Object>();
			// 数组类型的参数，都是以 Key 为 “ array ”放在语句参数中的。
			map.put("array", object);
			return map;
		}
		// 参数不是 List 或 数组时，返回原参数。
		return object;
	}

	/**
	 * <h3>HashMap 而已</h3>
	 * <p>
	 * 	只是覆写了 HashMap 的 Get 方法，当 Key 不存在的抛出异常。
	 * </p>
	 * <p>
	 * 使用的地方只是作为 MyBatis 的映射语句的参数。
	 * 因此它的“当 Key 不存在的抛出异常”这个功能发挥作用在映射语句中，当 OGNL 获取不在 Key 的值而造成的 SQL 语句结构错误被提早发现。
	 * </p>
	 * @author l
	 *
	 * @param <V>
	 */
	public static class StrictMap<V> extends HashMap<String, V> {

		private static final long serialVersionUID = -5741767162221585340L;

		/**
		 * 当 Key 不存在是，抛出 {@link BindingException} 异常。
		 */
		@Override
		public V get(Object key) {
			if (!super.containsKey(key)) {
				throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + this.keySet());
			}
			return super.get(key);
		}

	}

}
