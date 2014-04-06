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
 * <h3>事务隔离级别</h3>
 * <p>在JAVA中有两种事务： JDBC 事务和 JTA 事务。
 * 对于的事务隔离级别，JDBC 使用 {@link Connection#setTransactionIsolation(int)} 设置，其值只能为5个“ TRANSACTION_ ”为前缀的整形常量。</p>
 * <p>而 MyBatis 是以枚举的方式重新实现了这5个常量，去掉前缀剩余的部分为名称，值不变。
 * <p>如果没有显示调用 {@link Connection#setTransactionIsolation(int)} ，则 {@link Connection} 使用数据库默认的事务隔离级别，可以通过相应数据库的 SQL 语句进行查询，
 * 例如 MySQL 数据库可使用 select @@tx_isolation; 语句查看当前的事务隔离级别。</p>
 * 
 * <h3>事务隔离级别相关的概念</h3>
 * <ul>
 * <li>脏读：如果一个事务对数据进行了更新，但事务还没有提交，另一个事务就可以“看到”该事务没有提交的更新结果。这样造成的问题是，
 * 如果第一个事务回滚，那么第二个事务在此之前所“看到”的数据就是一笔脏数据。</li>
 * <li>不可重复读：指同个事务在整个事务过程中对同一笔数据进行读取，每次读取结果都不同。如果事务1在事务2的更新操作之前读取一次数据，
 * 在事务2的更新操作之后再读取同一笔数据一次，两次结果是不同的。所以TRANSACTION_READ_COMMITTED是无法避免不可重复读和虚读。</li>
 * <li>幻读：指同样一个查询在整个事务过程中多次执行后，查询所得的结果集是不一样的。幻读针对的是多笔记录。</li>
 * </ul>
 * 
 * 
 * <hr>
 * @author Clinton Begin
 */
public enum TransactionIsolationLevel {
	/**
	 * 表示不支持事务
	 */
	NONE(Connection.TRANSACTION_NONE),
	/**
	 * 表示脏读 （ dirty read ）、不可重复读和虚读 （phantom read）可以发生
	 */
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
	/**
	 * 表示不可重复读和虚读（phantom read）可以发生
	 */
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED), 
	/**
	 * 表示虚读（phantom read）可以发生
	 */
	REPEATABLE_READ(	Connection.TRANSACTION_REPEATABLE_READ), 
	/**
	 * 表示脏读 （ dirty read ）、不可重复读和虚读 （phantom read）都不可以发生
	 */
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	private final int level;

	private TransactionIsolationLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
