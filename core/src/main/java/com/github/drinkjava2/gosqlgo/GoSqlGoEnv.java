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

	public static final Map<String, Class<?>> gsgTemplates = new HashMap<String, Class<?>>();
 

	public static void registerGsgTemplate(String gsgMethod, Class<?> templateClass) {
		gsgTemplates.put(gsgMethod, templateClass);
	}

	private static String webappAbsoluteFolder; // servlet webapp absolute folder

	private static String deploy; // deploy package, store dynamic generated classed

	private static String deployAbsolutePath; // absolute path of deploy package

	private static String template; // template package name to store GoSqlGo template classes

	private static boolean isProduct = true; // if is product, reject compile files sent from front end

	static {
		InputStream is = DeployTool.class.getClassLoader().getResourceAsStream("GoSqlGo.properties");
		if (is == null) {
			System.err.println("Error: Config file GoSqlGo.properties not found.");
			System.exit(0);
		}
		Properties prop = new Properties();
		try {
			prop.load(is);
			deploy = prop.getProperty("deploy");
			template = prop.getProperty("template");

			String stage = prop.getProperty("stage");
			if ("product".equals(stage))
				isProduct = true;
			else if ("develop".equals(stage))
				isProduct = false;
			else
				throw new IllegalArgumentException("In GoSqlGo.properties, stage can only be develop or production");

			deployAbsolutePath = new File("").getAbsolutePath() + "/src/main/java/"
					+ GsgStrUtils.replace(deploy, ".", "/");
			webappAbsoluteFolder = new File("").getAbsolutePath() + "/src/main/webapp";
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return class stored in deploy package, if not found, return null
	 */
	public static Class<?> findStoredClass(String sqlJavaPiece) {
		if (sqlJavaPiece == null)
			return null;
		if (!GsgStrUtils.isLegalClassName(sqlJavaPiece))
			return null;
		return ClassExistCacheUtils
				.checkClassExist(new StringBuilder(GoSqlGoEnv.getDeploy()).append(".").append(sqlJavaPiece).toString());
	}

	/**
	 * @return the source code webapp folder
	 */
	public static String getSrcWebappFolder() {
		String srcWebappFolder = GsgStrUtils.replace(webappAbsoluteFolder, "\\", "/");
		srcWebappFolder = GsgStrUtils.replaceFirst(srcWebappFolder, "target/classes/", "");
		return srcWebappFolder;
	}

	/**
	 * @return the source code deploy folder
	 */
	public static String getSrcDeployFolder() {
		String srcWebappFolder = GsgStrUtils.replace(deployAbsolutePath, "\\", "/");
		srcWebappFolder = GsgStrUtils.replaceFirst(srcWebappFolder, "target/classes/", "");
		return srcWebappFolder;
	}

	// ==========getter & setter =============

	public static String getDeployAbsolutePath() {
		return deployAbsolutePath;
	}

	public static void setDeployAbsolutePath(String deployAbsolutePath) {
		GoSqlGoEnv.deployAbsolutePath = deployAbsolutePath;
	}

	public static String getDeploy() {
		return deploy;
	}

	public static void setDeploy(String deploy) {
		GoSqlGoEnv.deploy = deploy;
	}

	public static String getWebappAbsoluteFolder() {
		return webappAbsoluteFolder;
	}

	public static void setWebappAbsoluteFolder(String webappAbsoluteFolder) {
		GoSqlGoEnv.webappAbsoluteFolder = webappAbsoluteFolder;
	}

	public static String getTemplate() {
		return template;
	}

	public static void setTemplate(String template) {
		GoSqlGoEnv.template = template;
	}

	public static boolean isProduct() {
		return isProduct;
	}

	public static void setProduct(boolean isProduct) {
		GoSqlGoEnv.isProduct = isProduct;
	}

}
