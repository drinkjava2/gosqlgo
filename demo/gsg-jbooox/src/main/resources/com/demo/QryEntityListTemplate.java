package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQueryForEntityList;

import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.jdialects.ClassCacheUtils;
import com.github.drinkjava2.jsqlbox.SqlBoxException;

public class QryEntityListTemplate extends BaseTemplate {
	@Override
	public Object executeBody() {
		/* GSG BODY BEGIN */
		String sql = null;
		/* GSG BODY END */
		String entityClassName = GsgStrUtils.substringBefore(sql, ",");
		Class<?> entityClass = ClassCacheUtils.checkClassExist(entityClassName);
		SqlBoxException.assureNotNull(entityClass, "Entity class parameter can not be null");
		sql = GsgStrUtils.substringAfter(sql, ",");
		String[] paramArray = getParamArray();
		Object result;
		if (paramArray.length == 0)
			result = pQueryForEntityList(entityClass, sql);
		else
			result = pQueryForEntityList(entityClass, sql, paramArray);
		return result;
	}

}
