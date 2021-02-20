package com.demo;

import static com.github.drinkjava2.jsqlbox.DB.par;
import static com.github.drinkjava2.jsqlbox.DB.qryEntityList;
import static com.github.drinkjava2.jsqlbox.DB.qry;
import com.github.drinkjava2.jsqlbox.*;
import com.github.drinkjava2.gosqlgo.BaseTemplate;

import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.jdialects.ClassCacheUtils;
import com.github.drinkjava2.jsqlbox.DbException;

@SuppressWarnings("unused")
public class QryEntityListTemplate extends BaseTemplate {
    
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String entityClassName = GsgStrUtils.substringBefore(sql, ",");
		Class<?> entityClass = ClassCacheUtils.checkClassExist(entityClassName);
		DbException.assureNotNull(entityClass, "Entity class parameter can not be null");
		sql = GsgStrUtils.substringAfter(sql, ",");
		String[] paramArray = getParamArray();
		Object result;
		if (paramArray.length == 0)
			result = qryEntityList(entityClass, sql);
		else
			result = qryEntityList(entityClass, sql, par((Object[])paramArray));
		return result;
	}

}
