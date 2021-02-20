package com.demo;

import static com.github.drinkjava2.jsqlbox.DB.par;
import static com.github.drinkjava2.jsqlbox.DB.qryMap;
import static com.github.drinkjava2.jsqlbox.DB.qry;
import com.github.drinkjava2.jsqlbox.*;
import com.github.drinkjava2.gosqlgo.BaseTemplate;

@SuppressWarnings("unused")
public class QryMapTemplate extends BaseTemplate {
    
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return qryMap(sql);
		else
			return qryMap(sql, par((Object[]) paramArray));
	}

}
