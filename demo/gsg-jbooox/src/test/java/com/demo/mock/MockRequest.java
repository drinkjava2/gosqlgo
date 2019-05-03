/*
 * Copyright (C) 2016 Original Author
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.demo.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * Mock class of HttpServletRequest for unit test
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class MockRequest implements HttpServletRequest {
	Map<String, Object> attrs = new HashMap<String, Object>();
	private final Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();
	String requestURI;
	String method = "GET";
	String forwardOrIncludeUrl;

	@Override
	public Object getAttribute(String key) {
		return attrs.get(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attrs.put(key, value);
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	public HttpServletRequest setRequestURI(String requestURI) {
		this.requestURI = requestURI;
		return this;
	}

	@Override
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String url) {
		return new MockRequestDispatcher(url);
	}

	// ===============================================================
	// ====================garbage code below=========================
	// ===============================================================

	@Override
	public AsyncContext getAsyncContext() {

		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {

		return null;
	}

	@Override
	public String getCharacterEncoding() {

		return null;
	}

	@Override
	public int getContentLength() {

		return 0;
	}

	@Override
	public String getContentType() {

		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {

		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		return null;
	}

	@Override
	public String getLocalAddr() {

		return null;
	}

	@Override
	public String getLocalName() {

		return null;
	}

	@Override
	public int getLocalPort() {

		return 0;
	}

	@Override
	public Locale getLocale() {

		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {

		return null;
	}

	@Override
	public String getParameter(String name) {
		String[] arr = (name != null ? this.parameters.get(name) : null);
		return (arr != null && arr.length > 0 ? arr[0] : null);
	}

	public void addParameter(String name, String value) {
		addParameter(name, new String[] { value });
	}

	public void setParameter(String name, String value) {
		this.parameters.put(name, new String[] { value });
	}

	public void addParameter(String name, String... values) {
		if (name == null || name.length() == 0)
			throw new AssertionError("Parameter name must not be null");
		String[] oldArr = this.parameters.get(name);
		if (oldArr != null) {
			String[] newArr = new String[oldArr.length + values.length];
			System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
			System.arraycopy(values, 0, newArr, oldArr.length, values.length);
			this.parameters.put(name, newArr);
		} else {
			this.parameters.put(name, values);
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return this.parameters;
	}

	@Override
	public Enumeration<String> getParameterNames() {

		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {

		return null;
	}

	@Override
	public String getProtocol() {

		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {

		return null;
	}

	@Override
	public String getRealPath(String arg0) {

		return null;
	}

	@Override
	public String getRemoteAddr() {

		return null;
	}

	@Override
	public String getRemoteHost() {

		return null;
	}

	@Override
	public int getRemotePort() {

		return 0;
	}

	@Override
	public String getScheme() {

		return null;
	}

	@Override
	public String getServerName() {

		return null;
	}

	@Override
	public int getServerPort() {

		return 0;
	}

	@Override
	public ServletContext getServletContext() {

		return null;
	}

	@Override
	public boolean isAsyncStarted() {

		return false;
	}

	@Override
	public boolean isAsyncSupported() {

		return false;
	}

	@Override
	public boolean isSecure() {

		return false;
	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {

	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {

		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {

		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {

		return false;
	}

	@Override
	public String getAuthType() {

		return null;
	}

	@Override
	public String getContextPath() {

		return null;
	}

	@Override
	public Cookie[] getCookies() {

		return null;
	}

	@Override
	public long getDateHeader(String arg0) {

		return 0;
	}

	@Override
	public String getHeader(String arg0) {

		return null;
	}

	@Override
	public Enumeration<String> getHeaderNames() {

		return null;
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {

		return null;
	}

	@Override
	public int getIntHeader(String arg0) {

		return 0;
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {

		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {

		return null;
	}

	@Override
	public String getPathInfo() {

		return null;
	}

	@Override
	public String getPathTranslated() {

		return null;
	}

	@Override
	public String getQueryString() {

		return null;
	}

	@Override
	public String getRemoteUser() {

		return null;
	}

	@Override
	public StringBuffer getRequestURL() {

		return null;
	}

	@Override
	public String getRequestedSessionId() {

		return null;
	}

	@Override
	public String getServletPath() {

		return null;
	}

	@Override
	public HttpSession getSession() {

		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {

		return null;
	}

	@Override
	public Principal getUserPrincipal() {

		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {

		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {

		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {

		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {

		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {

		return false;
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {

	}

	@Override
	public void logout() throws ServletException {

	}

	public long getContentLengthLong() {
		return 0;
	}

	public String changeSessionId() {
		return null;
	}

	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		return null;
	}

}