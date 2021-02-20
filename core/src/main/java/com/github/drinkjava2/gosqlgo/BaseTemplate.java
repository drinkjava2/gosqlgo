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

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.drinkjava2.gosqlgo.util.GsgFileUtils;

/**
 * This is the base environment servlet store environment info
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public abstract class BaseTemplate {
    public static final String NONE = "GoSqlGo NONE TAG"; //used to customize output  
    public int code = 200;
    public String message = "";

    protected HttpServletRequest request; // request instance

    protected HttpServletResponse response; // response instance

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

    /** Equal to getRequest().getSession(); */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /** Equal to getRequest().getParameter(paramkey); */
    public String getParam(String paramkey) {
        return getRequest().getParameter(paramkey);
    }

    /** Equal to getRequest().getAttribute(attrKey); */
    public Object getAttr(String attrKey) {
        return getRequest().getAttribute(attrKey);
    }

    /** Pack all $1 , $2, ... to $100 parameters into a Map<String,String> */
    public Map<String, String> getParamMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 1; i <= 100; i++) {
            String parameter = getParam("$" + i);
            if (parameter != null)
                result.put("$" + i, parameter);
            else
                break;
        }
        return result;
    }

    /** Pack all $1 , $2,... to $100 parameters into a String[] */
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

    public static String httpPostOnURL(String theURL, Map<String, String> paramMap) {
        try {
            URL url = new URL(theURL);
            System.out.println("url=" + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();

            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : paramMap.entrySet())
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            sb.append("gsg=t");// end tag
            String body = sb.toString();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(body);
            writer.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                return GsgFileUtils.inputStreamToString(inputStream, "utf-8");
            }
            System.err.println("Error happen when access Node server on url:" + theURL);
            return "";
        } catch (Exception e) {
            System.err.println("Can not access Node server on URL:" + theURL);
            return "";
        }
    }

    /**
     * The body method for template
     * @return Object
     */
    public Object executeBody() {
        return null;
    }

    /**
     * Execute executeBody method and wrap result to a JsonResult result
     * @return JsonResult
     */
    public JsonResult execute() {
        Object data = executeBody();
        if (data instanceof JsonResult)
            return (JsonResult) data;
        return new JsonResult(code, message, data);
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
