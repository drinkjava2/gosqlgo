package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.*;
import com.demo.entity.*;
import javax.servlet.http.*;
import java.util.*;
import com.demo.page.Pages.*;
import com.alibaba.fastjson.*;
import com.github.drinkjava2.jwebbox.WebBox;  
import java.sql.Connection; 
import com.github.drinkjava2.jtransactions.tinytx.TinyTxConnectionManager;

@SuppressWarnings("all")
public class JavaTxTemplate extends BaseTemplate {
	
	public Object executeBody() {
		/* GSG BODY BEGIN */
		return null;
		/* GSG BODY END */
	} 
	
	@Override
	public WebBox execute() {
		Object result = null;
		// open transaction
		TinyTxConnectionManager tx = (TinyTxConnectionManager) gctx().getConnectionManager();
		try {
			tx.startTransaction(gctx().getDataSource(), Connection.TRANSACTION_READ_COMMITTED);
			result = executeBody();
			tx.commit(gctx().getDataSource());
		} catch (Exception e) {
			try {
				tx.rollback(gctx().getDataSource());
				System.out.println("Transaction rollbacked");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return new errorPage();
		}
		return object2WebBox(result);
	}

	
	
}
