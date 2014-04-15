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
package org.apache.ibatis.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * <h3>封装 MyBatis 映射文件中的一个映射语句的配置信息</h3>
 * <p>
 * 封装 MyBatis 映射文件中的一个映射语句，包括 &lt;insert /&gt;、&lt;delete /&gt;、&lt;update /&gt;、&lt;select /&gt; 的所有的信息，
 * 包括配置信息、执行的 SQL 语句、结果映射配置。
 * </p>
 * 
 * @author Clinton Begin
 */
public final class MappedStatement {

	/**
	 * 
	 */
	private String resource;
	/**
	 * 
	 */
	private Configuration configuration;
	/**
	 * <h3>对应配置中的 id 属性</h3>
	 * <p>
	 * 在命名空间中唯一的标识符,可以被用来引用这条语句。
	 * </p>
	 */
	private String id;
	/**
	 * <h3>对应配置中的 fetchSize 属性</h3>
	 * <p>
	 * 这是暗示驱动程序每次批量返回的结果行数。默认不设置(驱动自行处理)。
	 * </p>
	 */
	private Integer fetchSize;
	/**
	 * <h3>对应配置中的 timeout 属性</h3>
	 * <p>
	 * 这个设置驱动程序等待数据库返回请求结果,并抛出异常时间的 最大等待值。默认不设置(驱动自行处理)
	 * </p>
	 */
	private Integer timeout;
	/**
	 * <h3>对应配置中的 statementType 属性</h3>
	 * <p>
	 * STA TEMENT,PREPARED 或 CALLABLE 的一种。 这会让 MyBatis 使用选择使用 Statement， PreparedStatement 或 CallableStatement。 默认值:PREPARED。
	 * </p>
	 */
	private StatementType statementType;
	/**
	 * <h3>对应配置中的 resultSetType 属性</h3>
	 * <p>
	 * FORWARD_ONLY、SCROLL_SENSITIVE、SCROLL_INSENSITIVE 中的一种。默认是不设置(驱动自行处理)。
	 * </p>
	 */
	private ResultSetType resultSetType;
	/**
	 * <h3>执行动态计算和获取 SQL语句</h3>
	 * <p>不是映射文件中的配置信息，而是对配置信息计算出 SQL 语句。</p>
	 */
	private SqlSource sqlSource;
	/**
	 * <h3>缓存</h3>
	 * <p>
	 * </p>
	 */
	private Cache cache;
	/**
	 * <h3>查询参数</h3>
	 * 对应配置中的 parameterMap 属性
	 * <p>
	 * 这是引用外部 parameterMap 的已经被废弃的方法。使用内联参数 映射和 parameterType 属性。
	 * </p>
	 */
	private ParameterMap parameterMap;
	/**
	 * <h3>对应配置中的 resultMap 属性</h3>
	 * <p>
	 * 命名引用外部的 resultMap。 返回 map 是 MyBatis 最具力量的特性， 对其有一个很好的理解的话， 许多复杂映射的情形就能被解决了。 使用 resultMap 或 resultType ，但不能同时使用。
	 * </p>
	 */
	private List<ResultMap> resultMaps;
	/**
	 * <h3>对应配置中的 属性</h3>
	 * <p>
	 * </p>
	 */
	private boolean flushCacheRequired;
	/**
	 * <h3>对应配置中的 useCache 属性</h3>
	 * <p>
	 * 将其设置为 true, 将会导致本条语句的结果被缓存。 默认值: true。
	 * </p>
	 */
	private boolean useCache;
	/**
	 * <h3>对应配置中的 resultOrdered 属性</h3>
	 * <p>
	 * This is only applicable for nested result select statements: If this is true, it is assumed that nested results are contained or grouped together such
	 * that when a new main result row is returned, no references to a previous result row will occur anymore. This allows nested results to be filled much more
	 * memory friendly. Default: false.
	 * </p>
	 */
	private boolean resultOrdered;
	/**
	 * <h3>对应配置中的 属性</h3>
	 * <p>
	 * </p>
	 */
	private SqlCommandType sqlCommandType;
	/**
	 * <h3>对应配置中的 useGeneratedKeys 属性</h3>
	 * <p>
	 * ( 仅 对 insert, update 有 用 ) 这 会 告 诉 MyBatis 使 用 JDBC 的 getGeneratedKeys 方法来取出由数据（比如：像 MySQL 和 SQL Server 
	 * 这样的数据库管理系统的自动递增字段)内部生成的主键。 默认值:false。
	 * </p>
	 */
	private KeyGenerator keyGenerator;
	/**
	 * <h3>对应配置中的 keyProperty 属性</h3>
	 * <p>
	 * (仅对 insert, update 有用) 标记一个属性, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值。 默认: 不设置。 Can be a comma separated list of property names
	 * if multiple generated columns are expected.
	 * </p>
	 */
	private String[] keyProperties;
	/**
	 * <h3>对应配置中的 keyColumn 属性</h3>
	 * <p>
	 * (仅对 insert, update 有用) 标记一个属性, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值。 默认: 不设置。 Can be a comma separated list of columns names
	 * if multiple generated columns are expected.
	 * </p>
	 */
	private String[] keyColumns;
	/**
	 * <h3>对应配置中的 属性</h3>
	 * <p>
	 * </p>
	 */
	private boolean hasNestedResultMaps;
	/**
	 * <h3>对应配置中的 databaseId 属性</h3>
	 * <p>
	 * In case there is a configured databaseIdProvider, MyBatis will load all statements with no databaseId attribute or with a databaseId that matches the
	 * current one. If case the same statement if found with and without the databaseId the latter will be discarded.
	 * </p>
	 */
	private String databaseId;
	/**
	 * <h3>对应配置中的 属性</h3>
	 * <p>
	 * </p>
	 */
	private Log statementLog;
	/**
	 * <h3>对应配置中的 属性</h3>
	 * <p>
	 * </p>
	 */
	private LanguageDriver lang;
	/**
	 * <h3>对应配置中的 属性</h3>
	 * <p>
	 * </p>
	 */
	private String[] resultSets;

	private MappedStatement() {
		// constructor disabled
	}

	public static class Builder {
		private MappedStatement mappedStatement = new MappedStatement();

		public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
			mappedStatement.configuration = configuration;
			mappedStatement.id = id;
			mappedStatement.sqlSource = sqlSource;
			mappedStatement.statementType = StatementType.PREPARED;
			mappedStatement.parameterMap = new ParameterMap.Builder(configuration, "defaultParameterMap", null, new ArrayList<ParameterMapping>()).build();
			mappedStatement.resultMaps = new ArrayList<ResultMap>();
			mappedStatement.timeout = configuration.getDefaultStatementTimeout();
			mappedStatement.sqlCommandType = sqlCommandType;
			mappedStatement.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator()
					: new NoKeyGenerator();
			String logId = id;
			if (configuration.getLogPrefix() != null)
				logId = configuration.getLogPrefix() + id;
			mappedStatement.statementLog = LogFactory.getLog(logId);
			mappedStatement.lang = configuration.getDefaultScriptingLanuageInstance();
		}

		public Builder resource(String resource) {
			mappedStatement.resource = resource;
			return this;
		}

		public String id() {
			return mappedStatement.id;
		}

		public Builder parameterMap(ParameterMap parameterMap) {
			mappedStatement.parameterMap = parameterMap;
			return this;
		}

		public Builder resultMaps(List<ResultMap> resultMaps) {
			mappedStatement.resultMaps = resultMaps;
			for (ResultMap resultMap : resultMaps) {
				mappedStatement.hasNestedResultMaps = mappedStatement.hasNestedResultMaps || resultMap.hasNestedResultMaps();
			}
			return this;
		}

		public Builder fetchSize(Integer fetchSize) {
			mappedStatement.fetchSize = fetchSize;
			return this;
		}

		public Builder timeout(Integer timeout) {
			mappedStatement.timeout = timeout;
			return this;
		}

		public Builder statementType(StatementType statementType) {
			mappedStatement.statementType = statementType;
			return this;
		}

		public Builder resultSetType(ResultSetType resultSetType) {
			mappedStatement.resultSetType = resultSetType;
			return this;
		}

		public Builder cache(Cache cache) {
			mappedStatement.cache = cache;
			return this;
		}

		public Builder flushCacheRequired(boolean flushCacheRequired) {
			mappedStatement.flushCacheRequired = flushCacheRequired;
			return this;
		}

		public Builder useCache(boolean useCache) {
			mappedStatement.useCache = useCache;
			return this;
		}

		public Builder resultOrdered(boolean resultOrdered) {
			mappedStatement.resultOrdered = resultOrdered;
			return this;
		}

		public Builder keyGenerator(KeyGenerator keyGenerator) {
			mappedStatement.keyGenerator = keyGenerator;
			return this;
		}

		public Builder keyProperty(String keyProperty) {
			mappedStatement.keyProperties = delimitedStringtoArray(keyProperty);
			return this;
		}

		public Builder keyColumn(String keyColumn) {
			mappedStatement.keyColumns = delimitedStringtoArray(keyColumn);
			return this;
		}

		public Builder databaseId(String databaseId) {
			mappedStatement.databaseId = databaseId;
			return this;
		}

		public Builder lang(LanguageDriver driver) {
			mappedStatement.lang = driver;
			return this;
		}

		public Builder resulSets(String resultSet) {
			mappedStatement.resultSets = delimitedStringtoArray(resultSet);
			return this;
		}

		public MappedStatement build() {
			assert mappedStatement.configuration != null;
			assert mappedStatement.id != null;
			assert mappedStatement.sqlSource != null;
			assert mappedStatement.lang != null;
			mappedStatement.resultMaps = Collections.unmodifiableList(mappedStatement.resultMaps);
			return mappedStatement;
		}
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public String getResource() {
		return resource;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getId() {
		return id;
	}

	public boolean hasNestedResultMaps() {
		return hasNestedResultMaps;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public StatementType getStatementType() {
		return statementType;
	}

	public ResultSetType getResultSetType() {
		return resultSetType;
	}

	public SqlSource getSqlSource() {
		return sqlSource;
	}

	public ParameterMap getParameterMap() {
		return parameterMap;
	}

	public List<ResultMap> getResultMaps() {
		return resultMaps;
	}

	/**
	 * <h3>获取缓存</h3>
	 * <p>这个缓存不时全局共享的，是当前实例自己的。</p>
	 * @return
	 */
	public Cache getCache() {
		return cache;
	}

	public boolean isFlushCacheRequired() {
		return flushCacheRequired;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public boolean isResultOrdered() {
		return resultOrdered;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public String[] getKeyProperties() {
		return keyProperties;
	}

	public String[] getKeyColumns() {
		return keyColumns;
	}

	public Log getStatementLog() {
		return statementLog;
	}

	public LanguageDriver getLang() {
		return lang;
	}

	public String[] getResulSets() {
		return resultSets;
	}

	/**
	 * 
	 * @param parameterObject
	 * @return
	 */
	public BoundSql getBoundSql(Object parameterObject) {
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings == null || parameterMappings.size() <= 0) {
			boundSql = new BoundSql(configuration, boundSql.getSql(), parameterMap.getParameterMappings(), parameterObject);
		}

		// check for nested result maps in parameter mappings (issue #30)
		for (ParameterMapping pm : boundSql.getParameterMappings()) {
			String rmId = pm.getResultMapId();
			if (rmId != null) {
				ResultMap rm = configuration.getResultMap(rmId);
				if (rm != null) {
					hasNestedResultMaps |= rm.hasNestedResultMaps();
				}
			}
		}

		return boundSql;
	}

	/**
	 * 
	 * @param in
	 * @return
	 */
	private static String[] delimitedStringtoArray(String in) {
		if (in == null || in.trim().length() == 0) {
			return null;
		} else {
			String[] answer = in.split(",");
			return answer;
		}
	}

}
