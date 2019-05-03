package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.nQuery;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQuery;

import org.apache.commons.dbutils.handlers.ArrayHandler;

public class QryArrayTemplate extends BaseTemplate {
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return nQuery(new ArrayHandler(), sql);
		else
			return pQuery(new ArrayHandler(), sql, (Object[]) paramArray);
	}

}
