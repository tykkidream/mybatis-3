/*
 *    Copyright 2009-2013 the original author or authors.
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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;

/**
 * <h3>数据访问接口的代理对象生产工厂</h3>
 * <p>使用动态代理创建某一接口的实例。这个接口是用于数据访问接口，它符合 MyBatis 的要求规范，
 * 与数据库映射文件匹配。</p>
 * 
 * <p>这个类本身不复杂，具有以下特性：</p>
 * <ul>
 * <li>拥有一个 {@link Class} 类型的私有常量，保存被代理接口的类实例对象。</li>
 * <li>拥有一个 {@link Map}<{@link Method}, {@link MapperMethod}> 类型的私有变量，</li>
 * <li>创建代理对象的两个重载方法，使用了 JAVA 的动态代理技术 {@link Proxy} 类。</li>
 * </ul>
 * 
 * <p>通常一个接口对应一个本类实例即可，不需要两个多个，可以理解拥有为一个接口信息的对象、工具、实例（代理对象）的生产工厂。
 * 使用中无法共用的只有 {@link SqlSession} 和  {@link MapperProxy} 。
 * </p>
 * @author Lasse Voss
 */
public class MapperProxyFactory<T> {

	/**
	 * 被代理的接口
	 */
	private final Class<T> mapperInterface;
	private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();

	public MapperProxyFactory(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	/**
	 * 返回被代理的接口。
	 * @return
	 */
	public Class<T> getMapperInterface() {
		return mapperInterface;
	}

	/**
	 * 返回接口所有方法的反射的集合。
	 * @return
	 */
	public Map<Method, MapperMethod> getMethodCache() {
		return methodCache;
	}

	/**
	 * <h3>创建接口的代理对象</h3>
	 * <p>使用了 JAVA 的动态代理技术 {@link Proxy} 类创建代理对象，可参考 {@link Proxy#newProxyInstance Proxy.newProxyInstance} 方法，此处使用参数如下：</p>
	 * <ul>
	 * <li>Ⅰ、类加载器使用被代理接口的类加载器。</li>
	 * <li>Ⅱ、需要代理的接口只有此类实例管理的被代理接口一个。</li>
	 * <li>Ⅲ、{@link java.lang.reflect.InvocationHandler InvocationHandler} 的实例需要外部调用时传入。</li>
	 * </ul>
	 * @param mapperProxy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T newInstance(MapperProxy<T> mapperProxy) {
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
	}

	/**
	 * <h3>创建接口的代理对象</h3>
	 * <p>创建 {@link MapperProxy} 对象，并且把被代理接口的信息交给它使用。 包括 {@link SqlSession} 、接口的类实例、
	 * 接口所有方法的反射的集合。它是 {@link java.lang.reflect.InvocationHandler InvocationHandler} 的实例，
	 * 是 JAVA 动态代理技术的关键参数，之后就交给 {@link #newInstance(MapperProxy)} 方法创建代理对象。</p>
	 * 
	 * @param sqlSession
	 * @return
	 */
	public T newInstance(SqlSession sqlSession) {
		final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
		return newInstance(mapperProxy);
	}

}
