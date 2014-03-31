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
package org.apache.ibatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class InterceptorChain {

	private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

	/**
	 * <p>
	 * 简单来说，调用拦截器链里的每个拦截器依次对参数对象进行拦截。
	 * </p>
	 * <p>
	 * 在这里需要了解的是拦截器链，它实际上本类中 List&lt;Interceptor&gt; 类型的私有数据，可以参看 {@link #getInterceptors()} 了解更多的信息。
	 * </p>
	 * <p>
	 * 这里方法内容不多，循环拦截器链得到拦截器（ Interceptor 类型），执行拦截器的 plugin 方法对参数进行代理，将返回的代理对象返回。
	 * 一般情况下，生成参数的代理对象的 plugin 方法内部仅仅执行 Plugin.wrap(目标对象,拦截器) 处理即可，返回值即为代理对象。
	 * </p>
 	 * <p>
	 * 需要特别注意的是，循环是从可能没有被代理过的参数开始的，参数的另外一个作用就是一个引用，每次循环对这个引用的对象生成的新代理对象，
	 * 并将引用指向新代理对象，因此在下次循环时被代理的对象就不是一开始的参数，而是变成了刚刚生成的新代理对象，这样层层代理构成了一个代理链，
	 * 所以整个循环的作用是把参数被插件层层的嵌套、包装起来。
 	 * </p>
	 */
	public Object pluginAll(Object target) {
		for (Interceptor interceptor : interceptors) {
			target = interceptor.plugin(target);
		}
		return target;
	}

	public void addInterceptor(Interceptor interceptor) {
		interceptors.add(interceptor);
	}

	/**
	 * <h3>返回一个不可修改的拦截器链。</h3>
	 * <p>
	 * 在本类中，有一行代码： <br>&nbsp;&nbsp;&nbsp;&nbsp;private final List&lt;Interceptor&gt; interceptors = new ArrayList&lt;Interceptor&gt;();<br> 这个 interceptors 对象是用来保存插件，这些插件通过
	 * {@link org.apache.ibatis.session.Configuration Configuration} 读取 MyBatis 配置中的插件信息创建获取。
	 * </p>
	 * <p>
	 * 这个方法不是直接返回 interceptors 对象，而是使用 {@link java.util.Collections#unmodifiableList() Collections.unmodifiableList()} 处理返回它的不可修改视图。
	 * </p>
	 */
	public List<Interceptor> getInterceptors() {
		return Collections.unmodifiableList(interceptors);
	}

}
