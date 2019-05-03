/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Mock implementation of the {@link javax.servlet.http.HttpServletResponse}
 * interface.
 */
public class MockResponse implements HttpServletResponse {
	public ByteArrayOutputStream baos = new ByteArrayOutputStream();
	public PrintWriter writer = new PrintWriter(baos);

	@Override
	public void flushBuffer() throws IOException {
		// Auto-generated method stub

	}

	@Override
	public int getBufferSize() {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		// Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return writer;
	}

	@Override
	public boolean isCommitted() {
		// Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// Auto-generated method stub

	}

	@Override
	public void setBufferSize(int arg0) {
		// Auto-generated method stub

	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// Auto-generated method stub

	}

	@Override
	public void setContentLength(int arg0) {
		// Auto-generated method stub

	}

	@Override
	public void setContentLengthLong(long arg0) {
		// Auto-generated method stub

	}

	@Override
	public void setContentType(String arg0) {
		// Auto-generated method stub

	}

	@Override
	public void setLocale(Locale arg0) {
		// Auto-generated method stub

	}

	@Override
	public void addCookie(Cookie arg0) {
		// Auto-generated method stub

	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		// Auto-generated method stub

	}

	@Override
	public void addHeader(String arg0, String arg1) {
		// Auto-generated method stub

	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		// Auto-generated method stub

	}

	@Override
	public boolean containsHeader(String arg0) {
		// Auto-generated method stub
		return false;
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public String getHeader(String arg0) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public int getStatus() {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public void sendError(int arg0) throws IOException {
		// Auto-generated method stub

	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		// Auto-generated method stub

	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		// Auto-generated method stub

	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		// Auto-generated method stub

	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// Auto-generated method stub

	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		// Auto-generated method stub

	}

	@Override
	public void setStatus(int arg0) {
		// Auto-generated method stub

	}

	@Override
	public void setStatus(int arg0, String arg1) {
		// Auto-generated method stub

	}

}
