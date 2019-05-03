package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.nQueryForObject;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQueryForObject; 
 
public class QryTemplate extends BaseTemplate { 
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return nQueryForObject(sql);
		else
			return pQueryForObject(sql, paramArray);
	}

}
