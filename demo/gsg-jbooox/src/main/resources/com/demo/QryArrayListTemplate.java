package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.nQuery;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQuery;

import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class QryArrayListTemplate extends BaseTemplate {
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return nQuery(new ArrayListHandler(), sql);
		else
			return pQuery(new ArrayListHandler(), sql, (Object[]) paramArray);
	}

}
