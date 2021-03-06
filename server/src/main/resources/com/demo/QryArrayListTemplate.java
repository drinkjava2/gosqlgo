package com.demo;

import static com.github.drinkjava2.jsqlbox.DB.par;
import static com.github.drinkjava2.jsqlbox.DB.qry;
import com.github.drinkjava2.jsqlbox.*;
import com.github.drinkjava2.gosqlgo.BaseTemplate;
import org.apache.commons.dbutils.handlers.ArrayListHandler;


@SuppressWarnings("unused")
public class QryArrayListTemplate extends BaseTemplate {

    @Override
    public Object executeBody() {
        /* GSG BODY BEGIN */
        String sql = null;
        /* GSG BODY END */
        String[] paramArray = getParamArray();
        if (paramArray.length == 0)
            return qry(new ArrayListHandler(), sql);
        else
            return qry(new ArrayListHandler(), sql, par((Object[]) paramArray));
    }

}
