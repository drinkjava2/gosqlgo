package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.nQuery;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQuery;

import com.github.drinkjava2.jdbpro.handler.TitleArrayListHandler;

public class QryTitleArrayListTemplate extends BaseTemplate {
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return nQuery(new TitleArrayListHandler(), sql);
		else
			return pQuery(new TitleArrayListHandler(), sql, (Object[]) paramArray);
	}

}
