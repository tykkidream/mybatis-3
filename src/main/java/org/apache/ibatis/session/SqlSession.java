/*
 *    Copyright 2009-2011 the original author or authors.
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

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.executor.BatchResult;

/**
 * <h3>一次数据库会话</h3>
 * 
 * 
 * <hr>
 * The primary Java interface for working with MyBatis. Through this interface you can execute commands, get mappers and manage
 * transactions.
 * 
 * @author Clinton Begin
 */
public interface SqlSession extends Closeable {

	/**
	 * Retrieve a single row mapped from the statement key
	 * 
	 * @param <T>
	 *            the returned object type
	 * @param statement
	 * @return Mapped object
	 */
	<T> T selectOne(String statement);

	/**
	 * Retrieve a single row mapped from the statement key and parameter.
	 * 
	 * @param <T>
	 *            the returned object type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return Mapped object
	 */
	<T> T selectOne(String statement, Object parameter);

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter.
	 * 
	 * @param <E>
	 *            the returned list element type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @return List of mapped object
	 */
	<E> List<E> selectList(String statement);

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter.
	 * 
	 * @param <E>
	 *            the returned list element type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return List of mapped object
	 */
	<E> List<E> selectList(String statement, Object parameter);

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter, within the specified row bounds.
	 * 
	 * @param <E>
	 *            the returned list element type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param rowBounds
	 *            Bounds to limit object retrieval
	 * @return List of mapped object
	 */
	<E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

	/**
	 * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of the properties in the
	 * resulting objects. Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
	 * 
	 * @param <K>
	 *            the returned Map keys type
	 * @param <V>
	 *            the returned Map values type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param mapKey
	 *            The property to use as key for each value in the list.
	 * @return Map containing key pair data.
	 */
	<K, V> Map<K, V> selectMap(String statement, String mapKey);

	/**
	 * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of the properties in the
	 * resulting objects.
	 * 
	 * @param <K>
	 *            the returned Map keys type
	 * @param <V>
	 *            the returned Map values type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param mapKey
	 *            The property to use as key for each value in the list.
	 * @return Map containing key pair data.
	 */
	<K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

	/**
	 * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of the properties in the
	 * resulting objects.
	 * 
	 * @param <K>
	 *            the returned Map keys type
	 * @param <V>
	 *            the returned Map values type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param mapKey
	 *            The property to use as key for each value in the list.
	 * @param rowBounds
	 *            Bounds to limit object retrieval
	 * @return Map containing key pair data.
	 */
	<K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

	/**
	 * <h3></h3>
	 * 
	 * <hr>
	 * <p>
	 * Retrieve a single row mapped from the statement key and parameter using a {@code ResultHandler}.
	 * </p>
	 * <p>
	 * 检索一行数据的映射（查询一行数据并映射成 JavaBean 对象），它根据语句的Key（映射文件中的 namespace + 语句的 id ） 和参数由 {@code ResultHandler} 处理后获得。
	 * </p>
	 * 
	 * @param statement
	 *            <p>
	 *            Unique identifier matching the statement to use.
	 *            </p>
	 *            <p>
	 *            要执行的语句的唯一标识符。
	 *            </p>
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param handler
	 *            ResultHandler that will handle each retrieved row
	 * @return Mapped object
	 */
	void select(String statement, Object parameter, ResultHandler handler);

	/**
	 * Retrieve a single row mapped from the statement using a {@code ResultHandler}.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param handler
	 *            ResultHandler that will handle each retrieved row
	 * @return Mapped object
	 */
	void select(String statement, ResultHandler handler);

	/**
	 * Retrieve a single row mapped from the statement key and parameter using a {@code ResultHandler} and {@code RowBounds}
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param rowBounds
	 *            RowBound instance to limit the query results
	 * @param handler
	 *            ResultHandler that will handle each retrieved row
	 * @return Mapped object
	 */
	void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler);

	/**
	 * Execute an insert statement.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the insert.
	 */
	int insert(String statement);

	/**
	 * Execute an insert statement with the given parameter object. Any generated autoincrement values or selectKey entries will modify the
	 * given parameter object properties. Only the number of rows affected will be returned.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return int The number of rows affected by the insert.
	 */
	int insert(String statement, Object parameter);

	/**
	 * <p>Execute a update statement. The number of rows affected will be returned.</p>
	 * <p>执行 UPDATE 语句。受影响的行数将被返回。</p>
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the update.
	 */
	int update(String statement);

	/**
	 * <p>Execute a update statement. The number of rows affected will be returned.</p>
	 * <p>执行 UPDATE 语句。受影响的行数将被返回。</p>
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return int The number of rows affected by the update.
	 */
	int update(String statement, Object parameter);

	/**
	 * <p>Execute a delete statement. The number of rows affected will be returned.</p>
	 * <p>执行 DELETE 语句。受影响的行数将被返回。</p>
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the delete.
	 */
	int delete(String statement);

	/**
	 * <p>Execute a delete statement. The number of rows affected will be returned.</p>
	 * <p>执行 DELETE 语句。受影响的行数将被返回。</p>
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return int The number of rows affected by the delete.
	 */
	int delete(String statement, Object parameter);

	/**
	 * Flushes batch statements and commits database connection. Note that database connection will not be committed if no
	 * updates/deletes/inserts were called. To force the commit call {@link SqlSession#commit(boolean)}
	 */
	void commit();

	/**
	 * <p>Flushes batch statements and commits database connection.</p>
	 * <p>刷新批处理语句并提交数据库连接。</p>
	 * 
	 * @param force
	 *            forces connection commit
	 */
	void commit(boolean force);

	/**
	 * Discards pending batch statements and rolls database connection back. Note that database connection will not be rolled back if no
	 * updates/deletes/inserts were called. To force the rollback call {@link SqlSession#rollback(boolean)}
	 */
	void rollback();

	/**
	 * Discards pending batch statements and rolls database connection back. Note that database connection will not be rolled back if no
	 * updates/deletes/inserts were called.
	 * 
	 * @param force
	 *            forces connection rollback
	 */
	void rollback(boolean force);

	/**
	 * <p>Flushes batch statements.</p>
	 * <p>刷新批处理语句。</p>
	 * 
	 * @return BatchResult list of updated records
	 * @since 3.0.6
	 */
	List<BatchResult> flushStatements();

	/**
	 * <p>Closes the session</p>
	 * <p>关闭会话</p>
	 */
	void close();

	/**
	 * <p>Clears local session cache</p>
	 * <p>清除本地会话缓存</p>
	 */
	void clearCache();

	/**
	 * <p>
	 * Retrieves current configuration
	 * </p>
	 * <p>
	 * 获取当前的 {@link Configuration} 。
	 * </p>
	 * @return Configuration
	 */
	Configuration getConfiguration();

	/**
	 * <h3>获得 Mapper 实例</h3>
	 * <p>
	 * 获得的是 MyBatis 的 Mapper 接口的实例，而且这个实例必须和当前 {@link SqlSession} 关联。
	 * </p>
	 * <p>
	 * Mapper 实例最终由 {@link MapperRegistry#getMapper(Class, SqlSession)} 创建，一般使用 {@link Configuration#getMapper(Class, SqlSession)} 较简便·。
	 * 这两种方式都需要先获得 {@link Configuration} 实例，要知道通过本接口的 {@link #getConfiguration()} 就可获得 。本方法相比刚才的两个方法它少一
	 * 个本接口类型的参数，因为那个参数就是自己本身，这样减少一些复杂增加一些简便。
	 * </p>
	 * <hr>
	 * <p>
	 * Retrieves a mapper.
	 * </p>
	 * <p>
	 * 获取映射器。
	 * </p>
	 * 
	 * @param <T>
	 *            <p>
	 *            the mapper type
	 *            </p>
	 * @param type
	 *            <p>
	 *            Mapper interface class
	 *            </p>
	 *            <p>
	 *            必须是接口的类实例。
	 *            </p>
	 * @return a mapper bound to this SqlSession
	 */
	<T> T getMapper(Class<T> type);

	/**
	 * <p>
	 * Retrieves inner database connection
	 * </p>
	 * <p>
	 * 获取内部数据库连接。
	 * </p>
	 * 
	 * @return Connection
	 */
	Connection getConnection();
}
