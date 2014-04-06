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

/**
 * <p>执行器（ {@link org.apache.ibatis.executor.Executor Executor} ）执行 SQL的方式</p>
 * 
 * @author Clinton Begin
 */
public enum ExecutorType {
	/**
	 * 简单。{@link org.apache.ibatis.executor.Executor Executor} 使用的默认方式。
	 */
  SIMPLE, 
  /**
   * 重用。
   */
  REUSE, 
  /**
   * 批处理。
   */
  BATCH
}
