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

import com.alibaba.fastjson.JSON;
import com.demo.page.Pages.emptyPage;
import com.github.drinkjava2.gosqlgo.ServletTemplate;
import com.github.drinkjava2.jwebbox.WebBox;

/**
 * This is the super class of all other templates, execute method return a
 * WebBox instance, override executeBody method in each template class
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public abstract class BaseTemplate extends ServletTemplate {
	public Object executeBody() {
		return null;
	}

	public WebBox execute() {
		Object result = null;
		try {
			result = executeBody();
		} catch (Exception e) {
			e.printStackTrace();
			return new emptyPage();
		}
		return object2WebBox(result);
	}

	public static WebBox object2WebBox(Object result) {
		if (result == null)
			return new WebBox().setText("null");
		if (result instanceof String)
			return new WebBox().setText((String) result);
		if (result instanceof WebBox)
			return (WebBox) result;
		return new WebBox().setText(JSON.toJSONString(result));
	}

}
