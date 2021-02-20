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
import java.util.ArrayList;
import java.util.List;

/**
 * DeployTool extract all SQL and Java in html or .js files to server side, and
 * reverse.
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class DeployTool {

	public static void deploy(String option) {
		if ("goServer".equalsIgnoreCase(option))
		    goServForce();
		else if ("goFront".equalsIgnoreCase(option))
		    goFrontForce();
		else
			System.out
					.println("Error: Deploy option can only be 'goServer' or 'goFront'");
	}

	/**
	 * Register a customized GSG template
	 */
	public static void registerGsgTemplate(String gsgMethod, Class<?> templateClass) {
		GoSqlGoEnv.registerGsgTemplate(gsgMethod, templateClass);
	}

	/**
	 * Extract all Sql/Java pieces to server side, except which have "FRONT" keyword
	 * at beginning
	 */
	public static void goServ() {
		List<File> files = getHtmlJspJsFiles(GoSqlGoEnv.getSrcWebappFolder(), null);
		for (File file : files)
			DeployToolUtils.oneFileToServ(file, false);
	}

	/**
	 * Push back all Sql/Java pieces to front side, except which have "SERV" keyword
	 * at beginning
	 */
	public static void goFront() {
		List<File> htmlJspfiles = getHtmlJspJsFiles(GoSqlGoEnv.getSrcWebappFolder(), null);
		List<String> toDeleteJavas = new ArrayList<String>();
		for (File file : htmlJspfiles)
			DeployToolUtils.oneFileToFront(file, false, toDeleteJavas, false);
		for (String javaFile : toDeleteJavas)
			new File(javaFile).delete();
	}

	/**
	 * Push back all Sql/Java pieces to front side, ignore "SERV" keyword
	 */
	public static void goFrontForce() {
		List<File> htmlJspfiles = getHtmlJspJsFiles(GoSqlGoEnv.getSrcWebappFolder(), null);
		List<String> toDeleteJavas = new ArrayList<String>();
		for (File file : htmlJspfiles)
			DeployToolUtils.oneFileToFront(file, false, toDeleteJavas, true);
		for (String javaFile : toDeleteJavas)
			new File(javaFile).delete();
	}

	/**
	 * Extract all Sql/Java pieces to server side, no matter if it have "FRONT"
	 * keyword or not
	 */
	public static void goServForce() {
		List<File> files = getHtmlJspJsFiles(GoSqlGoEnv.getSrcWebappFolder(), null);
		for (File file : files)
			DeployToolUtils.oneFileToServ(file, true);
	}

	// ============static methods=============================

	private static List<File> getHtmlJspJsFiles(String path, List<File> files) {
		if (files == null)
			files = new ArrayList<File>();
		File file = new File(path);
		File[] array = file.listFiles();
		if (array == null)
			return files;
		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				String fileName = array[i].getName();
				if (fileName.endsWith(".html") || fileName.endsWith(".htm") || fileName.endsWith(".jsp"))
					files.add(array[i]);
			} else if (array[i].isDirectory()) {
				getHtmlJspJsFiles(array[i].getPath(), files);
			}
		}
		return files;
	}

}
