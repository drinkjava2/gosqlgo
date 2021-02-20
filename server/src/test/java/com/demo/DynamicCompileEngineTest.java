/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.demo;

import org.junit.Test;

import com.github.drinkjava2.gosqlgo.compile.DynamicCompileEngine;

import text.Texts.JavaA;
import text.Texts.JavaB;

/**
 * Unit test of Dao
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class DynamicCompileEngineTest {

	@Test
	public void testDynamicCompile() throws InstantiationException, IllegalAccessException {
		Class<?> classA = DynamicCompileEngine.instance.javaCodeToClass("com.demo.deploy.A", new JavaA().toString());
		System.out.println(classA.getName());
		System.out.println(classA.hashCode());
		System.out.println(classA.getClassLoader());

		Class<?> classB1 = DynamicCompileEngine.instance.javaCodeToClass("com.demo.deploy.B", new JavaB().toString());
		classB1.newInstance();
	}

}
