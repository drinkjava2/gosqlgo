package com.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.pQueryForEntityList;

import java.util.List;

import com.github.drinkjava2.gosqlgo.util.GsgStrUtils;
import com.github.drinkjava2.jdialects.ClassCacheUtils;
import com.github.drinkjava2.jsqlbox.SqlBoxException;

public class QryEntityTemplate extends BaseTemplate {
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
		List<Object> result;
		if (paramArray.length == 0)
			result = pQueryForEntityList(entityClass, sql);
		else
			result = pQueryForEntityList(entityClass, sql, paramArray);
		if (result == null || result.size() == 0)
			return null;
		else
			return result.get(0);
	}

}
