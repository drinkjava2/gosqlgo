package com.demo;

import static com.github.drinkjava2.jsqlbox.DB.par;
import static com.github.drinkjava2.jsqlbox.DB.qry;
import com.github.drinkjava2.gosqlgo.BaseTemplate;
import com.github.drinkjava2.jdbpro.handler.TitleArrayListHandler;
import com.github.drinkjava2.jsqlbox.DB;
import com.github.drinkjava2.gosqlgo.BaseTemplate;


@SuppressWarnings("unused")
public class QryTitleArrayListTemplate extends BaseTemplate {
    
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String[] paramArray = getParamArray();
		if (paramArray.length == 0)
			return qry(new TitleArrayListHandler(), sql);
		else
			return qry(new TitleArrayListHandler(), sql, par((Object[]) paramArray));
	}

}
