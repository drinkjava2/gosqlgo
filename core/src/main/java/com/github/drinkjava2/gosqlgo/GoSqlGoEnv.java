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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.drinkjava2.gosqlgo.util.ClassExistCacheUtils;
import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;

/**
 * DeployTool extract all SQL and Java in html or .js files to server side, and
 * reverse.
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class GoSqlGoEnv {// NOSONAR

    private static final Map<String, Class<?>> gsgTemplates = new HashMap<String, Class<?>>();

    private static final String deploy_package; // deploy package name, store dynamic generated classed

    private static final String project_root_folder; // absolute path of deploy package

    private static final String stage; // product or develop. If set to product, reject receive SQL and Java from front end

    private static final boolean debug_info; // product or develop. If set to true will return debug info to front end

    private static final boolean is_product_stage;

    private static final boolean java_file_export; // if export java class source file in classes/.../deploy folder

    private static final String develop_token;

    private static final TokenSecurity tokenSecurity;

    //private static final AbstractBaseTemplate baseTemplate;

    static {
        InputStream is = DeployTool.class.getClassLoader().getResourceAsStream("gosqlgo.properties");
        if (is == null) {
            System.err.println("Error: Config file gosqlgo.properties not found.");
            System.exit(0);
        }
        Properties prop = new Properties();
        try {
            prop.load(is);
            deploy_package = prop.getProperty("deploy_package");
            //TEMPLATE_PACKAGE = prop.getProperty("template_package");

            stage = prop.getProperty("stage");
            if ("product".equalsIgnoreCase(stage) || "develop".equalsIgnoreCase(stage)) {
            } else
                throw new IllegalArgumentException("In gosqlgo.properties, stage can only be develop or product");
            is_product_stage = "product".equalsIgnoreCase(stage);

            String devTokenStr = prop.getProperty("develop_token");
            if (devTokenStr == null)
                devTokenStr = "";
            develop_token = devTokenStr;

            String token_security = prop.getProperty("token_security");
            tokenSecurity = (TokenSecurity) Class.forName(token_security).newInstance();

            String debug_info_str = prop.getProperty("debug_info");
            if ("true".equalsIgnoreCase(debug_info_str) || "false".equalsIgnoreCase(debug_info_str)) {
            } else
                throw new IllegalArgumentException("In gosqlgo.properties, debug_info can only be true or false");
            debug_info = "true".equalsIgnoreCase(prop.getProperty("debug_info"));

            if ("true".equalsIgnoreCase(prop.getProperty("java_file_export")))
                java_file_export = true;
            else
                java_file_export = false;

            String newFilePath = new File("").getAbsolutePath();
            newFilePath = GsgStrUtils.substringBefore(newFilePath, "\\target");
            project_root_folder = GsgStrUtils.substringBefore(newFilePath, "/target");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register customized GoSqlGo template class
     * 
     * @param gsgMethod
     * @param templateClass
     */
    public static void registerGsgTemplate(String gsgMethod, Class<?> templateClass) {
        gsgTemplates.put(gsgMethod, templateClass);
    }

    /**
     * Return class stored in deploy package, if not found, return null
     */
    public static Class<?> findCachedClass(String sqlJavaPiece) {
        if (sqlJavaPiece == null)
            return null;
        if (!GsgStrUtils.isLegalClassName(sqlJavaPiece))
            return null;
        return ClassExistCacheUtils.checkClassExist(new StringBuilder(GoSqlGoEnv.getDeployPackage()).append(".").append(sqlJavaPiece).toString());
    }

    public static String getClassesDeployFolder() {
        return getClassLoaderFolder() + "/" + GsgStrUtils.replace(deploy_package, ".", "/");
    }

    public static String getSrcDeployFolder() {
        return getProjectRootFolder() + "/src/main/java/" + GsgStrUtils.replace(deploy_package, ".", "/");
    }

    public static String getSrcWebappFolder() {
        return getProjectRootFolder() + "/src/main/webapp";

    }

    public static String getClassLoaderFolder() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        path = GsgStrUtils.replaceFirst(path, "file:/", "");
        path = GsgStrUtils.replaceFirst(path, "file:", "");
        if (path.endsWith("/") || path.endsWith("\\"))
            path = path.substring(0, path.length() - 1);
        return path;
    }

    // ==========getter & setter =============

    public static Map<String, Class<?>> getGsgtemplates() {
        return gsgTemplates;
    }

    public static String getDeployPackage() {
        return deploy_package;
    }

    public static String getProjectRootFolder() {
        return project_root_folder;
    }

    public static boolean isProductStage() {
        return is_product_stage;
    }

    public static boolean isDevelopStage() {
        return !is_product_stage;
    }

    public static boolean isDebugInfo() {
        return debug_info;
    }

    public static boolean isJavaFileExport() {
        return java_file_export;
    }

    public static String getDevelopToken() {
        return develop_token;
    }

    public static TokenSecurity getTokenSecurity() {
        return tokenSecurity;
    }

}
