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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/*
 * Builds {@link SqlSession} instances.
 *
 */
/**
 * <h3>简化创建 {@link SqlSessionFactory}</h3>
 * <p>
 * 只有9个重载的 build 方法，目的都是为了创建 {@link SqlSessionFactory} 对象，是设计模式中 Builder 模式的方式，有以下特点：
 * </p>
 * <ul>
 * <li>其关键在于 {@link Configuration} 对象，由 {@link #build(Configuration)} 来处理。</li>
 * <li>其它的8个方法，都是将含有配置信息的 XML 处理等到 {@link Configuration} 对象，再交由 {@link #build(Configuration)} 来处理。</li>
 * <li>其它的8个方法中最主要的是 {@link #build(InputStream, String, Properties)} 和 {@link #build(Reader, String, Properties)} 。这两个实现了主要的逻辑， 区别仅是 XML
 * 的输入流不同，一个是字节输入流，一个是字符输入流，逻辑一模一样。</li>
 * <li>剩余的6个仅是提供了默认参数的重载方法而已。</li>
 * </ul>
 * 
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {

	/**
	 * <p>
	 * 仅是简单的重载，真正的逻辑在 {@link #build(Reader reader, String environment, Properties properties)} 中，其中参数 environment 为 null ， properties 为 null
	 * 。
	 * </p>
	 * 
	 * @param reader
	 *            含有配置信息的 XML格式的字符输入流
	 * @return {@link #build(Reader,String,Properties) build}(reader, null, null);
	 */
	public SqlSessionFactory build(Reader reader) {
		return build(reader, null, null);
	}

	/**
	 * <p>
	 * 仅是简单的重载，真正的逻辑在 {@link #build(Reader reader, String environment, Properties properties)} 中，其中参数 properties 为 null 。
	 * </p>
	 * 
	 * @param reader
	 *            含有配置信息的 XML格式的字符输入流
	 * @param environment
	 *            要使用“环境配置”的ID，必须存在于第一个参数的配置信息中
	 * @return {@link #build(Reader,String,Properties) build}(reader, environment, null)
	 */
	public SqlSessionFactory build(Reader reader, String environment) {
		return build(reader, environment, null);
	}

	/**
	 * <p>
	 * 仅是简单的重载，真正的逻辑在 {@link #build(Reader reader, String environment, Properties properties)} 中，其中参数 environment 为 null 。
	 * </p>
	 * 
	 * @param reader
	 *            含有配置信息的 XML格式的字符输入流
	 * @param properties
	 *            属性参数配置
	 * @return {@link #build(Reader,String,Properties) build}(reader, null, properties)
	 */
	public SqlSessionFactory build(Reader reader, Properties properties) {
		return build(reader, null, properties);
	}

	/**
	 * <p>
	 * 拥有独自逻辑过程的重载方法，主要做了两件事：
	 * </p>
	 * <ol>
	 * <li>根据所有参数实例化 {@link XMLConfigBuilder} 对象，由它解析 XML 信息并构建出 {@link Configuration} 对象。</li>
	 * <li>传递 {@link Configuration} 对象给 {@link #build(Configuration)} 实例化 {@link SqlSessionFactory} 对象。</li>
	 * </ol>
	 * <p>
	 * 对于参数详细情况，参考 {@link XMLConfigBuilder#XMLConfigBuilder(Reader, String, Properties) XMLConfigBuilder(Reader, String, Properties)} 。
	 * </p>
	 * 
	 * @param reader
	 *            含有配置信息的 XML格式的字符输入流
	 * @param environment
	 *            要使用“环境配置”的ID，必须存在于第一个参数的配置信息中
	 * @param properties
	 *            属性参数配置
	 * @return {@link SqlSessionFactory}
	 */
	public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
		try {
			// 1、 构建能解析 XML 信息的 XMLConfigBuilder 。
			XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
			// 2、 parser.parse() 解析XML信息，获得 Configuration 对象。
			// 3、 传递 Configuration 对象构建 DefaultSqlSessionFactory 对象。
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			try {
				reader.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}

	/**
	 * <p>
	 * 仅是简单的重载，真正的逻辑在 {@link #build(InputStream inputStream, String environment,Properties properties)} 中，其中参数 environment 默认设为 null ，
	 * properties 默认设为 null。
	 * </p>
	 * 
	 * @param inputStream
	 *            含有配置信息的 XML格式的字节输入流
	 * @return build(inputStream, null, null);
	 */
	public SqlSessionFactory build(InputStream inputStream) {
		return build(inputStream, null, null);
	}

	/**
	 * <p>
	 * 仅是简单的重载，真正的逻辑在 {@link #build(InputStream inputStream, String environment,Properties properties)} 中，其中参数 properties 默认设为 null 。
	 * </p>
	 * 
	 * @param inputStream
	 *            含有配置信息的 XML格式的字节输入流
	 * @param environment
	 *            要使用“环境配置”的ID，必须存在于第一个参数的配置信息中
	 * @return build(inputStream, environment, null);
	 */
	public SqlSessionFactory build(InputStream inputStream, String environment) {
		return build(inputStream, environment, null);
	}

	/**
	 * <p>
	 * 仅是简单的重载，真正的逻辑在 {@link #build(InputStream inputStream, String environment,Properties properties)} 中，其中参数 environment 默认设为 null 。
	 * </p>
	 * 
	 * @param inputStream
	 *            含有配置信息的 XML格式的字节输入流
	 * @param properties
	 *            属性参数配置
	 * @return build(inputStream, null, properties);
	 */
	public SqlSessionFactory build(InputStream inputStream, Properties properties) {
		return build(inputStream, null, properties);
	}

	/**
	 * <p>
	 * 拥有独自逻辑过程的重载方法，主要做了两件事：
	 * </p>
	 * <ol>
	 * <li>根据所有参数实例化 {@link XMLConfigBuilder} 对象，由它解析 XML 信息并构建出 {@link Configuration} 对象。</li>
	 * <li>传递 {@link Configuration} 对象给 {@link #build(Configuration)} 实例化 {@link SqlSessionFactory} 对象。</li>
	 * </ol>
	 * <p>
	 * 对于参数详细情况，参考 {@link XMLConfigBuilder#XMLConfigBuilder(InputStream, String, Properties) XMLConfigBuilder(InputStream, String,
	 * Properties)} 。
	 * </p>
	 * 
	 * @param inputStream
	 *            含有配置信息的 XML格式的字节输入流
	 * @param environment
	 *            要使用“环境配置”的ID，必须存在于第一个参数的配置信息中
	 * @param properties
	 *            属性参数配置
	 * @return {@link SqlSessionFactory}
	 */
	public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
		try {
			// 1、 构建能解析 XML 信息的 XMLConfigBuilder 。
			XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
			// 2、 parser.parse() 解析XML信息，获得 Configuration 对象。
			// 3、 传递 Configuration 对象构建 DefaultSqlSessionFactory 对象。
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			try {
				inputStream.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}

	/**
	 * <p>
	 * 实例化 {@link SqlSessionFactory} 对象。由 {@link Configuration} 对象创建 {@link DefaultSqlSessionFactory} 对象。
	 * </p>
	 * 
	 * @param config
	 *            拥有配置信息的 {@link Configuration} 对象
	 * @return {@link SqlSessionFactory}
	 */
	public SqlSessionFactory build(Configuration config) {
		return new DefaultSqlSessionFactory(config);
	}

}
