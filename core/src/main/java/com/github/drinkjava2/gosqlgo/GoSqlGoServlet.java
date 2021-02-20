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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.github.drinkjava2.gosqlgo.compile.DynamicCompileEngine;
import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.jwebbox.WebBox;

/**
 * Dispatch *.gsg call to local java classes and return a JSON
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class GoSqlGoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAction(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAction(req, resp);
    }

    public static void doAction(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*"); //allow cross origin access 
        resp.setHeader("Access-Control-Allow-Methods", "*");
        resp.setHeader("Access-Control-Max-Age", "7200");
        resp.addHeader("Access-Control-Allow-Headers", "*");
        resp.setHeader("Access-Control-Allow-Credentials", "*");
        resp.setCharacterEncoding("utf-8");

        JsonResult jsonResult = doActionBody(req, resp);
        Integer status = jsonResult.getStatus();
        if (status != null)
            resp.setStatus(status);
        else
            resp.setStatus(200);
        jsonResult.setStatus(null); //no need put status in json

        if (BaseTemplate.NONE.equals(jsonResult.getData())) { // if return NONE, do nothing
            return;
        } else if (jsonResult.getData() instanceof WebBox) { //if data is WebBox instance, use it output html page
            resp.setHeader("Content-Type", "text/html; charset=utf-8");
            WebBox web = (WebBox) jsonResult.getData();
            web.show(req, resp);
            return;
        } else {//default return json 
            resp.setHeader("Content-Type", "application/json;charset:utf-8");
            String json = JSON.toJSONString(jsonResult);
            PrintWriter out = null;
            try {
                out = resp.getWriter();
                out.println(json);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null)
                    out.close();
            }
        }
    }

    /** Dispatch all .gsg call to gsg classes, and return a json */
    public static JsonResult doActionBody(HttpServletRequest req, HttpServletResponse resp) {
        if ("true".equals(req.getParameter("login"))) {
            String token = GoSqlGoEnv.getTokenSecurity().login(req.getParameter("username"), req.getParameter("password"));
            if (!GsgStrUtils.isEmpty(token))
                return new JsonResult(200, "login success", token);
            else
                return new JsonResult(403, "login fail", "").setStatus(403);
        }
        if (GoSqlGoEnv.isDevelopStage()) {
            String develop_token = req.getParameter("develop_token");
            if (GsgStrUtils.isEmpty(develop_token))
                return JsonResult.json403("Error: develop_token is required in develop stage", req);
            else if (!develop_token.equals(GoSqlGoEnv.getDevelopToken()))
                return JsonResult.json403("Error: incorrect develop_token", req);
        }

        String sqlOrJavaPiece = req.getParameter("$0");
        if (GsgStrUtils.isEmpty(sqlOrJavaPiece))
            return JsonResult.json403("Error: request is empty.", req);

        Class<?> childClass = null;
        try {
            childClass = GoSqlGoEnv.findCachedClass(sqlOrJavaPiece);
            if (childClass == null) {
                if (GoSqlGoEnv.isProductStage())
                    return JsonResult.json403("Error: in product stage but not found class on server.", req);
                String gsgMethod = req.getParameter("gsgMethod");
                PieceType pieceType = PieceType.byGsgMethod(gsgMethod);
                Class<?> templateClass = GoSqlGoEnv.getGsgtemplates().get(gsgMethod);
                if (templateClass == null)
                    return JsonResult.json403("Error: template class for gsg method '" + gsgMethod + "' not found.", req);
                SqlJavaPiece piece = SqlJavaPiece.parseFromFrontText(gsgMethod, sqlOrJavaPiece);
                String classSrc = SrcBuilder.createSourceCode(templateClass, pieceType, piece);
                childClass = DynamicCompileEngine.instance.javaCodeToClass(GoSqlGoEnv.getDeployPackage() + "." + piece.getClassName(), classSrc);
            }
            if (childClass == null) //still is null
                return JsonResult.json403("Error: compile failed on server side.", req);

            String methodId = GsgStrUtils.substringBefore(childClass.getSimpleName(), "_");
            if (!GoSqlGoEnv.getTokenSecurity().allowExecute(req.getParameter("token"), methodId))
                return JsonResult.json403("Error: no privilege to execute '" + methodId + "' method", req);

            BaseTemplate instance = null;
            if (BaseTemplate.class.isAssignableFrom(childClass))
                instance = (BaseTemplate) childClass.newInstance();
            else
                return JsonResult.json403("Error: incorrect GoSqlGo child template error.", req);

            instance.initParams(req, resp);

            //Here do token check

            return instance.execute();
        } catch (Exception e) {
            if (GoSqlGoEnv.isDebugInfo()) //if debugInfo is true, will put exception message and debug info in JSON
                return new JsonResult(403, "Error: server internal error.").setStatus(403).setDebugInfo(JsonResult.getDebugInfo(req) + "\n" + e.getMessage());
            else
                return JsonResult.json403("Error: server internal error.", req);
        }
    }

}
