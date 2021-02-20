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

    /**
     * According templateClass, pieceType, frontText to generate java source code
     * 
     * @param templateClass
     * @param pieceType
     * @param frontText
     * @return java source code
     */
    public static String createSourceCode(Class<?> templateClass, PieceType pieceType, String frontText) {
        SqlJavaPiece piece = SqlJavaPiece.parseFromFrontText(pieceType.toString(), frontText);
        return createSourceCode(templateClass, pieceType, piece);
    }

 
    /**
     * According templateClass, pieceType, SqlJavaPiece to generate java source code
     * 
     * @param templateClass
     * @param piectType
     * @param sqlJavaPiece
     * @return
     */
    public static String createSourceCode(Class<?> templateClass, PieceType piectType, SqlJavaPiece sqlJavaPiece) {
        if (piectType == null)
            throw new NullPointerException("PieceType can not be null when create source code");
        String classSrc;

        classSrc = TxtUtils.getJavaSourceCodeUTF8(templateClass);
        classSrc = GsgStrUtils.substringAfter(classSrc, "package ");
        classSrc = GsgStrUtils.substringAfter(classSrc, ";");
        classSrc = sqlJavaPiece.getImports() + "\n" + classSrc;
        classSrc = "package " + GoSqlGoEnv.getDeployPackage() + ";\n" + classSrc;
        String classDeclar = GsgStrUtils.substringBetween(classSrc, "public ", "{");
        classSrc = GsgStrUtils.replaceFirst(classSrc, classDeclar, "class " + sqlJavaPiece.getClassName() + " extends " + templateClass.getName());

        if (PieceType.JAVA.equals(piectType)) {
            classSrc = GsgStrUtils.replaceOneBetween(classSrc, "/* GSG BODY BEGIN */", "/* GSG BODY END */", sqlJavaPiece.getBody());
        } else if (PieceType.QRY.equals(piectType)) {
            String sql = sqlJavaPiece.getBody();
            sql = GsgStrUtils.replace(sql, "\\`", "`");
            sql = GsgStrUtils.replace(sql, "\"", "\\\"");
            classSrc = GsgStrUtils.replaceOneBetween(classSrc, "/* GSG BODY BEGIN */", "/* GSG BODY END */", "\n" + "		String sql = \"" + sql + "\";" + "\n		");
        } else
            throw new IllegalArgumentException("Unknow PieceType when create Java source code");
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
        throw new IllegalArgumentException("Unknow PieceType when create front text");
    }

    /** build Front Leading Tags */
    public static String buildFrontLeadingTagsAndImports(SqlJavaPiece piece) {
        StringBuilder sb = new StringBuilder();
        if (!GsgStrUtils.isEmpty(piece.getId()))
            sb.append("#").append(piece.getId()).append(" ");
        if (!GsgStrUtils.isEmpty(piece.getImports()))
            sb.append(piece.getImports()).append(" ");
        return sb.toString();
    }

}
