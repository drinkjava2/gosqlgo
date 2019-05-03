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

import static com.github.drinkjava2.gosqlgo.util.GsgStrUtils.isEmpty;
import static com.github.drinkjava2.gosqlgo.util.GsgStrUtils.positionOfPureChar;
import static com.github.drinkjava2.gosqlgo.util.GsgStrUtils.substringAfter;
import static com.github.drinkjava2.gosqlgo.util.GsgStrUtils.substringBefore;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.drinkjava2.gosqlgo.compile.DynamicCompileEngine;
import com.github.drinkjava2.gosqlgo.util.GsgFileUtils;
import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.gosqlgo.util.Systemout;

/**
 * Store static methods for DeployTool
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class DeployToolUtils {

	/**
	 * Extract sql/java to server side for one html/javascript file, if forceDeploy
	 * is true, ignore if have FRONT control word, force extract to server side
	 */
	public static void oneFileToServ(File file, boolean forceDeploy) {
		String text = GsgFileUtils.readFile(file.getAbsolutePath(), "UTF-8");
		Map<String, SqlJavaPiece> formatedMap = new LinkedHashMap<String, SqlJavaPiece>();
		boolean changed = false;
		String formated = text;

		for (Entry<String, Class<?>> entry : GoSqlGoEnv.gsgTemplates.entrySet()) {
			String gsgMethod = entry.getKey();
			String gsgMtd_ = "$" + gsgMethod + "(`";
			Class<?> templateClass = entry.getValue();
			formatedMap.clear();
			formated = formatText(file, formated, formatedMap, gsgMtd_, '`');

			for (Entry<String, SqlJavaPiece> item : formatedMap.entrySet()) {
				changed = true;
				String key = item.getKey();
				SqlJavaPiece piece = item.getValue();
				if (piece.isFront() && !forceDeploy)
					formated = GsgStrUtils.replaceFirst(formated, key, gsgMtd_ + piece.getOriginText() + "`");
				else {
					String className = piece.getClassName();
					String src = SrcBuilder.createSourceCode(templateClass, PieceType.byGsgMethod(gsgMethod), piece);
					if (piece.isFull()) {
						String srcFile = GsgStrUtils.substringBefore(GoSqlGoEnv.getSrcDeployFolder(), "main/java");
						String classFullName = piece.getClassName();
						if (!GsgStrUtils.isEmpty(piece.getPackageName()))
							classFullName = piece.getPackageName() + "." + classFullName;
						srcFile += "main/java/" + GsgStrUtils.replace(classFullName, ".", "/") + ".java";
						GsgFileUtils.writeFile(srcFile, src, "UTF-8");
						formated = GsgStrUtils.replaceFirst(formated, key, "$gsg('FULL " + classFullName + "'");
					} else {
						GsgFileUtils.writeFile(GoSqlGoEnv.getSrcDeployFolder() + "/" + className + ".java", src,
								"UTF-8");
						formated = GsgStrUtils.replaceFirst(formated, key, "$gsg('" + className + "'");
					}
				}
			}
		}

		if (changed) {
			GsgFileUtils.writeFile(file.getAbsolutePath(), formated, "UTF-8");
			Systemout.println("goServ => " + file.getAbsolutePath());
		}
	}

	private static String formatText(File file, String oldText, Map<String, SqlJavaPiece> map, String start,
			char endChar) {
		String result = oldText;
		boolean needTransfer = result.contains(start);
		while (needTransfer) {
			String front = substringBefore(result, start);
			String left = substringAfter(result, start);
			if (isEmpty(left))
				throw new IllegalArgumentException(
						"Error: " + start + " not closed ` in file: " + file.getAbsolutePath());

			int pos = positionOfPureChar(left, endChar);
			if (pos == -1)
				throw new IllegalArgumentException(
						"Error: " + start + " not found end ` in file: " + file.getAbsolutePath());

			String piece = left.substring(0, pos);
			String leftover = left.substring(pos + 1);
			SqlJavaPiece parsed = SqlJavaPiece.parseFromFrontText(piece);
			String key = "key" + GsgStrUtils.getRandomString(20);
			map.put(key, parsed);
			result = front + key + leftover;
			needTransfer = result.contains(start);
		}
		return result;
	}

	/**
	 * Push back sql/java from server side to HTML/JS, if forceGoFront is true,
	 * ignore if have SERV control word, force push back to front
	 */
	public static void oneFileToFront(File file, boolean forceGoFront, List<String> toDeleteJavas, boolean force) {
		String text = GsgFileUtils.readFile(file.getAbsolutePath(), "UTF-8");
		if (!text.contains("$gsg('"))
			return;

		boolean changed = false;
		Map<String, SqlJavaPiece> map = new LinkedHashMap<String, SqlJavaPiece>();
		String formated = formatText(file, text, map, "$gsg('", '\'');
		for (Entry<String, SqlJavaPiece> item : map.entrySet()) {
			changed = true;
			String key = item.getKey();
			SqlJavaPiece piece = item.getValue(); 
			String javaSrcFileName; 
			if (piece.getOriginText().startsWith("FULL ")) {
				String classFullName = GsgStrUtils.substringAfter(piece.getOriginText(), " ");
				String rootFolder = GsgStrUtils.substringBefore(GoSqlGoEnv.getSrcDeployFolder(), "main/java");
				javaSrcFileName = rootFolder + "main/java/" + GsgStrUtils.replace(classFullName, ".", "/") + ".java";
				String src = GsgFileUtils.readFile(javaSrcFileName, "UTF-8");
				if (src.contains("// GSG SERV\n")) {
					if (force) {
						src = GsgStrUtils.replaceFirst(src, "// GSG SERV\n", "SERV ");
						formated = GsgStrUtils.replaceFirst(formated, key, "$java(`FULL " + src + "`");
						toDeleteJavas.add(javaSrcFileName);
					} else
						formated = GsgStrUtils.replaceFirst(formated, key, "$gsg('" + piece.getOriginText() + "'");
				} else {
					formated = GsgStrUtils.replaceFirst(formated, key, "$java(`FULL " + src + "`");
					toDeleteJavas.add(javaSrcFileName);
				}
			} else {
				javaSrcFileName = GoSqlGoEnv.getSrcDeployFolder() + "/" + piece.getOriginText() + ".java";
				String src = GsgFileUtils.readFile(javaSrcFileName, "UTF-8");
				String template = GsgStrUtils.substringBetween(src, "extends", "Template");
				template = GsgStrUtils.substringAfterLast(template, ".");
				String gsgMethod = GsgStrUtils.toLowerCaseFirstOne(template);
				SqlJavaPiece newPiece = SqlJavaPiece.parseFromJavaSrcFile(javaSrcFileName);
				if (GsgStrUtils.isEmpty(piece.getOriginText()) || (newPiece.isServ() && !forceGoFront))
					formated = GsgStrUtils.replaceFirst(formated, key, "$gsg('" + piece.getOriginText() + "'");
				else {
					String newPieceStr = SrcBuilder.createFrontText(PieceType.byGsgMethod(gsgMethod), newPiece);
					formated = GsgStrUtils.replaceFirst(formated, key, "$" + gsgMethod + "(`" + newPieceStr + "`");
					toDeleteJavas.add(javaSrcFileName);
				}
			}

		}
		map.clear();

		if (changed) {
			GsgFileUtils.writeFile(file.getAbsolutePath(), formated, "UTF-8");
			Systemout.println("goFront => " + file.getAbsolutePath());
		}
	}

}
