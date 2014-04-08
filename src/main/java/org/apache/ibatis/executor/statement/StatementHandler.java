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
package org.apache.ibatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

/**
 * <h3>语句执行器</h3>
 * <p>
 * 执行语句（ execute statement ）的处理器。属于最底层，因为实现本接口需要直接使用 JDBC 。
 * </p>
 * <p>
 * <i>用于执行静态 SQL 语句并返回它所生成结果的对象。（这句话抄自 JDK6 API 的中文文档中 java.sql.Statement 接口说明的第一行。）</i>
 * </p>
 * 
 * @author Clinton Begin
 */
public interface StatementHandler {

	Statement prepare(Connection connection) throws SQLException;

	void parameterize(Statement statement) throws SQLException;

	void batch(Statement statement) throws SQLException;

	int update(Statement statement) throws SQLException;

	<E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

	BoundSql getBoundSql();

	ParameterHandler getParameterHandler();

}
