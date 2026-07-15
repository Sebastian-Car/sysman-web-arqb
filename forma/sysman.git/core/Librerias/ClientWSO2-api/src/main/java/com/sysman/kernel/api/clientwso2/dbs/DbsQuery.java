package com.sysman.kernel.api.clientwso2.dbs;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un elemento &lt;query&gt; de un archivo .dbs: el SQL tal
 * cual esta declarado (con parametros con nombre ":NOMBRE"), la lista
 * de parametros esperados y el id logico del datasource a usar.
 */
public class DbsQuery {

    private final String id;
    private final String sql;
    private final String dataSourceConfigId;
    private final List<DbsParam> params = new ArrayList<>();

    public DbsQuery(String id, String sql, String dataSourceConfigId) {
        this.id = id;
        this.sql = sql;
        this.dataSourceConfigId = dataSourceConfigId;
    }

    public void addParam(DbsParam param) {
        params.add(param);
    }

    public String getId() {
        return id;
    }

    public String getSql() {
        return sql;
    }

    public String getDataSourceConfigId() {
        return dataSourceConfigId;
    }

    public List<DbsParam> getParams() {
        return params;
    }
}
