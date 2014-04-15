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
package org.apache.ibatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * <h3>动态代理的调用处理</h3>
 * <p>
 * 本类实现了{@link InvocationHandler} 接口，被用于 Java 的动态代理技术，
 * 发挥的功能是为动态实例（ MyBatis 的 Mapper 接口的动态实例）方法调用时的处理。
 * 所以这个类中只需关注  {@link #invoke(Object, Method, Object[])} 方法。
 * </p>
 * <p>
 * 了解 Java 的动态代理就会知道一个 {@link InvocationHandler} 实例对应一个代理实例， 
 * </p>
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

	private static final long serialVersionUID = -6424540398559729838L;
	
	/**
	 * 
	 */
	private final SqlSession sqlSession;
	private final Class<T> mapperInterface;
	private final Map<Method, MapperMethod> methodCache;

	public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
		this.sqlSession = sqlSession;
		this.mapperInterface = mapperInterface;
		this.methodCache = methodCache;
	}

	/**
	 * <h3>处理 Mapper 实例的方法调用的请求</h3>
	 * <ol>
	 * <li>首先排除属于 {@link Object} 的方法，其它的都被视为 Mapper 的方法。</li>
	 * <li>通过 {@link Method} 获得 {@link MapperMethod} ，了解 {@link #cachedMapperMethod(Method)} 的信息。</li>
	 * <li>传递 {@link SqlSession} （内部的私有变量） 、调用 Mapper 的方法参数给 {@link MapperMethod} 执行操作数据的动作。</li>
	 * </ol>
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 如果方法的声明在 Object 类中。
		if (Object.class.equals(method.getDeclaringClass())) {
			try {
				// 执行方法原本的功能。
				return method.invoke(this, args);
			} catch (Throwable t) {
				throw ExceptionUtil.unwrapThrowable(t);
			}
		}
		// 到这里说明方法是属于被代理接口的。
		final MapperMethod mapperMethod = cachedMapperMethod(method);
		// 传递参数给映射语句对象执行。
		return mapperMethod.execute(sqlSession, args);
	}

	/**
	 * <h3>通过 Method 获得 MapperMethod </h3>
	 * @param method
	 * @return
	 */
	private MapperMethod cachedMapperMethod(Method method) {
		//
		MapperMethod mapperMethod = methodCache.get(method);
		if (mapperMethod == null) {
			mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
			methodCache.put(method, mapperMethod);
		}
		return mapperMethod;
	}

}
