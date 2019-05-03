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

import org.junit.Assert;
import org.junit.Test;

import com.demo.mock.MockRequest;
import com.demo.mock.MockResponse;
import com.github.drinkjava2.gosqlgo.SqlJavaPiece;
import com.github.drinkjava2.jwebbox.WebBox;

import text.Texts.JavaPiece;

/**
 * Unit test of Dao
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class DaoTest {

	@Test
	public void testParse() {
		String java = new JavaPiece().toString();
		SqlJavaPiece piece = SqlJavaPiece.parseFromFrontText(java);
		Assert.assertEquals(true, piece.isServ());
		Assert.assertEquals(true, piece.isFront());
		Assert.assertEquals("#GetAmount", piece.getId());
		System.out.println(piece.getImports());
		Assert.assertEquals(" import java.lang.Object; // GSG IMPORT\n", piece.getImports());
	}

	@Test
	public void testQry() {
		InitConfig.initDataBase();
		InitConfig.initGoSqlGoTemplates();
		MockRequest req = new MockRequest();
		MockResponse resp = new MockResponse();
		req.setParameter("gsgMethod", "qry");
		req.setParameter("$0",
				"SERV #Classname import java.lang.Object; import java.lang.String;  select amount from account where id=? and amount>=?");
		req.setParameter("$1", "A");
		req.setParameter("$2", "0");
		WebBox box = GsgDispatch.buildWebBox(req, resp);
		System.out.println(box.getText());
		Assert.assertEquals("500", box.getText());
	}

	@Test
	public void testJava() {
		InitConfig.initDataBase();
		InitConfig.initGoSqlGoTemplates();
		MockRequest req = new MockRequest();
		MockResponse resp = new MockResponse();
		req.setParameter("gsgMethod", "javaTx");
		req.setParameter("$0", "" + new JavaPiece());
		req.setParameter("$1", "A");
		req.setParameter("$2", "B");
		req.setParameter("$3", "100");
		WebBox box = GsgDispatch.buildWebBox(req, resp);
		Assert.assertEquals("Transfer Success!|400|600", box.getText());
	}

}
