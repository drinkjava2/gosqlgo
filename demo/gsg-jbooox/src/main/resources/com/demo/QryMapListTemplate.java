package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.nQueryForMapList;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQueryForMapList;

public class QryMapListTemplate extends BaseTemplate {
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return nQueryForMapList(sql);
		else
			return pQueryForMapList(sql, (Object[]) paramArray);
	}

}
