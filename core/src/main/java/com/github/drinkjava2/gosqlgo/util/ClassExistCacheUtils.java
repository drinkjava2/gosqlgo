/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.gosqlgo.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassCacheUtils is utility class to cache some info of classes read and write
 * method
 * 
 * @author Yong Zhu (Yong9981@gmail.com)
 * @since 1.0.0
 */
public abstract class ClassExistCacheUtils {// NOSONAR
	// To check if a class exist, if exist, cache it to avoid check again
	protected static ConcurrentHashMap<String, Class<?>> classExistCache = new ConcurrentHashMap<String, Class<?>>();

	protected static class ClassOrMethodNotExist {// NOSONAR
	}

	/** * Check class if exist, if exit return it, otherwise return null */
	public static Class<?> checkClassExist(String className) {
		Class<?> result = classExistCache.get(className);
		if (result != null) {
			if (ClassOrMethodNotExist.class.equals(result))
				return null;
			else
				return result;
		}
		try {
			result = Class.forName(className);
			if (result != null)
				classExistCache.put(className, result);
			else
				classExistCache.put(className, ClassOrMethodNotExist.class);
			return result;
		} catch (Exception e) {
			classExistCache.put(className, ClassOrMethodNotExist.class);
			return null;
		}
	}

	public static void registerClass(Class<?> clazz) {
		classExistCache.put(clazz.getName(), clazz);
	}

}
