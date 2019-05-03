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
package com.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.page.Pages.emptyPage;
import com.github.drinkjava2.gosqlgo.GoSqlGoEnv;
import com.github.drinkjava2.gosqlgo.PieceType;
import com.github.drinkjava2.gosqlgo.SqlJavaPiece;
import com.github.drinkjava2.gosqlgo.SrcBuilder;
import com.github.drinkjava2.gosqlgo.compile.DynamicCompileEngine;
import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.gosqlgo.util.Systemout;
import com.github.drinkjava2.jwebbox.WebBox;

/**
 * Dispatch *.gsg call to JSON or HTML piece
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class GsgDispatch extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebBox box = buildWebBox(req, resp);
		box.show(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebBox box = buildWebBox(req, resp);
		box.show(req, resp);
	}

	/** Dispatch all .gsg call to a WebBox */
	public static WebBox buildWebBox(HttpServletRequest req, HttpServletResponse resp) {
		String sqlOrJavaPiece = req.getParameter("$0");
		if (GsgStrUtils.isEmpty(sqlOrJavaPiece))
			return new emptyPage();
		Class<?> childClass = null;
		try {
			childClass = GoSqlGoEnv.findStoredClass(sqlOrJavaPiece);
			if (GoSqlGoEnv.isProduct()) {
				if (childClass == null) {
					Systemout.print("Error: in product stage but did not find class stored on server: "
							+ sqlOrJavaPiece.substring(0, 50) + " ...");
					return new emptyPage();
				}
			} else if (childClass == null) {
				String gsgMethod = req.getParameter("gsgMethod");
				PieceType pieceType = PieceType.byGsgMethod(gsgMethod);				 
				Class<?> templateClass = GoSqlGoEnv.gsgTemplates.get(gsgMethod);
				if (templateClass == null)
					throw new IllegalArgumentException("Template class for gsgMethod '"+gsgMethod+"' not found");
				SqlJavaPiece piece = SqlJavaPiece.parseFromFrontText(sqlOrJavaPiece);
				String classSrc = SrcBuilder.createSourceCode(templateClass, pieceType, piece);
				if (piece.isFull()) {
					childClass = DynamicCompileEngine.instance
							.javaCodeToClass(piece.getPackageName() + "." + piece.getClassName(), classSrc);
				} else
					childClass = DynamicCompileEngine.instance
							.javaCodeToClass(GoSqlGoEnv.getDeploy() + "." + piece.getClassName(), classSrc);
			} 
			BaseTemplate childInstance = null;
			if (childClass != null && BaseTemplate.class.isAssignableFrom(childClass))
				childInstance = (BaseTemplate) childClass.newInstance();
			else
				return new emptyPage();
			childInstance.initParams(req, resp);
			return childInstance.execute();
		} catch (Throwable e) {
			e.printStackTrace();
			return new emptyPage();
		}

	}

}
