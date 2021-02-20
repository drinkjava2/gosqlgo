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

import static com.github.drinkjava2.gosqlgo.util.GsgStrUtils.getRandomClassName;
import static com.github.drinkjava2.gosqlgo.util.GsgStrUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.drinkjava2.gosqlgo.util.GsgFileUtils;
import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;

/**
 * SQL or Java source code piece, this is a virtual model represents the sql or
 * java piece extracted from html file or remote ajax call
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class SqlJavaPiece {
	String id = null; // null: id is random, "#xxxxxxx":assign ID, "#":assign id by computer
	boolean serv = false; // sql or java piece should always stay at server side
	boolean front = false; // sql or java piece should always stay at front, except use deploy command
	boolean full = false; // if true mean it's a full java class source code
	String imports = ""; // import abc.def; import abc.hij;...
	String body = ""; // the SQL or Java piece body text
	String originText = ""; // the origin text to be parsed
	String methodType = ""; // can only be SQL or JAVA
	String packageName = ""; // if is "FULL" type, will parse package name
	String className = ""; // class name, like "DemoUser"

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImports() {
		return imports;
	}

	public void setImports(String imports) {
		this.imports = imports;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getOriginText() {
		return originText;
	}

	public void setOriginText(String originText) {
		this.originText = originText;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * parse a sql/java front text to SqlJavaPiece Object usage: <br/>
	 * SqlJavaPiece p = SqlJavaPiece.parse("#234234 Front import a.b.C; import
	 * b.d.e; import b.d.e; select * from users ");
	 * 
	 */
	public static SqlJavaPiece parseFromFrontText(String gsgMethod, String frontText) {
		SqlJavaPiece piece = doParse(frontText);
		if (!piece.getClassName().isEmpty())
			return piece;
		if (isEmpty(piece.getId()))
			piece.setClassName("Default_"+getRandomOrCachedClassName(gsgMethod, frontText));
		else
			piece.setClassName(GsgStrUtils.replace(piece.getId(), "#", "")+"_"+getRandomOrCachedClassName(gsgMethod, frontText));
		return piece;
	}

	// Cache random IDs, for developing stage only
	private static final Map<String, String> cachedRandomIdMap = new ConcurrentHashMap<String, String>();

	private static String getRandomOrCachedClassName(String gsgMethod, String frontText) {
		String key = gsgMethod + ":" + frontText;
		String id = cachedRandomIdMap.get(key);
		if (GsgStrUtils.isEmpty(id)) {
			id = getRandomClassName(20);
			cachedRandomIdMap.put(key, id);
		}
		return id;
	}

	/**
	 * Parse front text to SqlJavaPiece object
	 * 
	 * @param frontText
	 * @return SqlJavaPiece
	 */
	private static SqlJavaPiece doParse(String frontText) {
		SqlJavaPiece piece = new SqlJavaPiece();
		piece.setOriginText(frontText);
		if (GsgStrUtils.isEmpty(frontText))
			return piece;
		String lastPiece = frontText;
		String trimed = GsgStrUtils.trimLeadingWhitespace(lastPiece);
		String firstWord = GsgStrUtils.findFirstWordNoWhiteChars(trimed);
		while (!GsgStrUtils.isEmpty(firstWord)) {
			if (firstWord.startsWith("#"))
				piece.setId(firstWord);
			else if ("import".equals(firstWord)) { // NOSONAR
				// a.b; import b.c; select * from users; // NOSONAR
				StringBuilder imports = new StringBuilder();
				while ("import".equals(firstWord)) {
					String importStr = GsgStrUtils.substringBefore(lastPiece, ";");
					String stmp=(importStr+"; // GSG IMPORT").trim();
					imports.append(stmp).append("\n");
					lastPiece = lastPiece.substring(importStr.length() + 1);// import a.b; import c.d
					trimed = lastPiece.trim();
					firstWord = GsgStrUtils.findFirstWordNoWhiteChars(trimed);
                    if (!"import".equals(firstWord)) {
                        piece.setImports(imports.toString());
                        piece.setBody(lastPiece);
                        return piece;
                    }
				}
            } else {// Not imports found
                piece.setBody(lastPiece);
                return piece;
            }
			lastPiece = trimed.substring(firstWord.length());
			trimed = GsgStrUtils.trimLeadingWhitespace(lastPiece);
			firstWord = GsgStrUtils.findFirstWordNoWhiteChars(trimed);
		}
		 piece.setBody(lastPiece);
		return piece;
	}

	public static SqlJavaPiece parseFromJavaSrcFile(String fileFullPath) {
		SqlJavaPiece piece = new SqlJavaPiece();
		String src = GsgFileUtils.readFile(fileFullPath, "UTF-8");
		piece.setOriginText(src);
		if (GsgStrUtils.isEmpty(src))
			return piece;
		String code = GsgStrUtils.substringBetween(src, "/* GSG BODY BEGIN */", "/* GSG BODY END */");
		if (!GsgStrUtils.isEmpty(code))
			piece.setMethodType("JAVA");
		if (GsgStrUtils.isEmpty(code))
			return piece;

		piece.setBody(code);

		piece.setId(null);
		String imports = "";
		while (src.contains("// GSG IMPORT")) {
			String st = GsgStrUtils.substringBefore(src, "// GSG IMPORT");
			st = GsgStrUtils.substringAfterLast(st, "import ");
			imports += "\n"+("import " + st).trim();
			src = GsgStrUtils.replaceFirst(src, "// GSG IMPORT", "");
		}
		piece.setImports(imports);
		return piece;
	}

}
