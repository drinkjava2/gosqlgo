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

import static com.github.drinkjava2.jsqlbox.JSQLBOX.gctx;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.h2.jdbcx.JdbcConnectionPool;

import com.demo.entity.Account;
import com.github.drinkjava2.gosqlgo.GoSqlGoEnv;
import com.github.drinkjava2.jdialects.Dialect;
import com.github.drinkjava2.jdialects.StrUtils;
import com.github.drinkjava2.jsqlbox.SqlBoxContext;
import com.github.drinkjava2.jtransactions.tinytx.TinyTxConnectionManager;

/**
 * InitConfig should run only once at beginning of application to initialize
 * Database and global default SqlBoxContext
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class InitConfig extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		initDataBase();
		initGoSqlGoTemplates();
	}

	public static void initDataBase() {
		SqlBoxContext.resetGlobalVariants();
		JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:DBName" + StrUtils.getRandomString(30)
				+ ";MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0", "sa", "");
		SqlBoxContext.setGlobalNextDialect(Dialect.H2Dialect);
		SqlBoxContext.setGlobalNextAllowShowSql(true);
		Dialect.setGlobalAllowReservedWords(true);
		SqlBoxContext ctx = new SqlBoxContext(ds);
		ctx.setConnectionManager(TinyTxConnectionManager.instance());// 事务相关
		SqlBoxContext.setGlobalSqlBoxContext(ctx);// 设定全局缺省上下文
		for (String ddl : ctx.toCreateDDL(Account.class))// 第一次要建表
			gctx().nExecute(ddl);
		new Account().setId("A").setAmount(500).insert();// 准备测试数据
		new Account().setId("B").setAmount(500).insert();
		new Account().setId("C").setAmount(500).insert();
	}

	public static void initGoSqlGoTemplates() {
		GoSqlGoEnv.registerGsgTemplate("qry", QryTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("java", JavaTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("javaTx", JavaTxTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryArray", QryArrayTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryArrayList", QryArrayListTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryTitleArrayList", QryTitleArrayListTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryMap", QryMapTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryMapList", QryMapListTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryEntity", QryEntityListTemplate.class);
		GoSqlGoEnv.registerGsgTemplate("qryEntityList", QryEntityListTemplate.class); 
	}

}
