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
package com.github.drinkjava2.gosqlgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This is the base environment store servlet environment info. currently
 * version GoSqlGO depends on JSP .
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public abstract class ServletTemplate {
	protected String $0;
	protected String $1;
	protected String $2;
	protected String $3;
	protected String $4;
	protected String $5;
	protected String $6;
	protected String $7;
	protected String $8;
	protected String $9;
	protected String $10;
	protected String $11;
	protected String $12;
	protected String $13;
	protected String $14;
	protected String $15;
	protected String $16;
	protected String $17;
	protected String $18;
	protected String $19;
	protected String $20;

	protected HttpServletRequest request; // request instance

	protected HttpServletResponse response; // response instance

	public HttpSession getSession() {
		return getRequest().getSession();
	}

	public Map<String, String[]> getParameterMap() {
		return getRequest().getParameterMap();
	}

	public String getParam(String paramkey) {
		return getRequest().getParameter(paramkey);
	}

	public Object getAttr(String attrKey) {
		return getRequest().getAttribute(attrKey);
	}

	public String[] getParamArray() {
		List<String> paramList = new ArrayList<String>();
		for (int i = 1; i <= 100; i++) {
			String parameter = getParam("$" + i);
			if (parameter != null)
				paramList.add(parameter);
			else
				break;
		}
		return paramList.toArray(new String[paramList.size()]);
	}

	public void initParams(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		$0 = getParam("$0");
		$1 = getParam("$1");
		$2 = getParam("$2");
		$3 = getParam("$3");
		$4 = getParam("$4");
		$5 = getParam("$5");
		$6 = getParam("$6");
		$7 = getParam("$7");
		$8 = getParam("$8");
		$9 = getParam("$9");
		$10 = getParam("$10");
		$11 = getParam("$11");
		$12 = getParam("$12");
		$13 = getParam("$13");
		$14 = getParam("$14");
		$15 = getParam("$15");
		$16 = getParam("$16");
		$17 = getParam("$17");
		$18 = getParam("$18");
		$19 = getParam("$19");
		$20 = getParam("$20");
	}

	// getter & setters ==========

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

}
