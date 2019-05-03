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

import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.gosqlgo.util.TxtUtils;

/**
 * The SrcBuilder will build child class source code based on given template
 * class
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public abstract class SrcBuilder { // NOSONAR

	public static String createSourceCode(Class<?> templateClass, PieceType type, String frontText) {
		SqlJavaPiece piece = SqlJavaPiece.parseFromFrontText(frontText);
		return createSourceCode(templateClass, type, piece);
	}

	public static String createSourceCode(Class<?> templateClass, PieceType piectType, SqlJavaPiece piece) {
		if (piectType == null)
			throw new NullPointerException("PieceType can not be null when create source code");
		String classSrc;
		if (piece.isFull()) {
			classSrc = piece.getBody();
			if (piece.isServ())
				classSrc = "// GSG SERV\n" + classSrc;
		} else {
			classSrc = TxtUtils.getJavaSourceCodeUTF8(templateClass);
			classSrc = GsgStrUtils.substringAfter(classSrc, "package ");
			classSrc = GsgStrUtils.substringAfter(classSrc, ";");
			classSrc = buildGsgTagsForJavaSourceCode(piece) + "\n" + classSrc;
			classSrc = piece.getImports() + "\n" + classSrc;
			classSrc = "package " + GoSqlGoEnv.getDeploy() + ";\n" + classSrc;
			String classDeclar = GsgStrUtils.substringBetween(classSrc, "public ", "{");
			classSrc = GsgStrUtils.replaceFirst(classSrc, classDeclar,
					"class " + piece.getClassName() + " extends " + templateClass.getName());

			if (PieceType.JAVA.equals(piectType)) {
				classSrc = GsgStrUtils.replaceOneBetween(classSrc, "/* GSG BODY BEGIN */", "/* GSG BODY END */",
						piece.getBody());
			} else if (PieceType.QRY.equals(piectType)) {
				String sql = piece.getBody();
				sql = GsgStrUtils.replace(sql, "\\`", "`");
				sql = GsgStrUtils.replace(sql, "\"", "\\\"");
				classSrc = GsgStrUtils.replaceOneBetween(classSrc, "/* GSG BODY BEGIN */", "/* GSG BODY END */",
						"\n" + "		String sql = \"" + sql + "\";" + "\n		");
			}
		}
		return classSrc;
	}

	public static String createFrontText(PieceType pieceType, SqlJavaPiece piece) {
		if (PieceType.JAVA.equals(pieceType)) {
			String head = buildFrontLeadingTagsAndImports(piece);
			String body = piece.getBody();
			if (head.length() > 0 && body != null && body.length() > 0 && body.charAt(0) == ' ')
				head = head.substring(0, head.length() - 1);
			return head + body;
		} else if (PieceType.QRY.equals(pieceType)) {
			String sql = piece.getBody();
			sql = GsgStrUtils.substringAfter(sql, "\"");
			sql = GsgStrUtils.substringBeforeLast(sql, "\"");
			sql = GsgStrUtils.replace(sql, "`", "\\`").trim();
			return buildFrontLeadingTagsAndImports(piece) + sql;
		}
		throw new NullPointerException("Unknow error when create front text");
	}

	/** Build GSG TAGS for java source code */
	public static String buildGsgTagsForJavaSourceCode(SqlJavaPiece piece) {
		StringBuilder sb = new StringBuilder();
		if (piece.isServ())
			sb.append("\n// GSG SERV");
		if (piece.isFront())
			sb.append("\n// GSG FRONT");
		if (piece.isFull())
			sb.append("\n// GSG FULL");
		if (!GsgStrUtils.isEmpty(piece.getId()))
			sb.append("\n // GSG ID = \"").append(GsgStrUtils.replaceFirst(piece.getId(), "#", "")).append("\";");
		return sb.toString();
	}

	/** build Front Leading Tags */
	public static String buildFrontLeadingTagsAndImports(SqlJavaPiece piece) {
		StringBuilder sb = new StringBuilder();
		if (piece.isServ())
			sb.append("SERV ");
		if (piece.isFront())
			sb.append("FRONT ");
		if (piece.isFull())
			sb.append("FULL ");
		if (!GsgStrUtils.isEmpty(piece.getId()))
			sb.append("#").append(piece.getId()).append(" ");
		if (!GsgStrUtils.isEmpty(piece.getImports()))
			sb.append(piece.getImports()).append(" ");
		return sb.toString();
	}

}
