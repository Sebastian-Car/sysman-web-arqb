package com.sysman.kernel.api.clientwso2.dbs;

/**
 * Representa un &lt;param name="..." sqlType="..."/&gt; dentro de un
 * &lt;query&gt; de un archivo .dbs.
 */
public class DbsParam {

    private final String name;
    private final String sqlType;

    public DbsParam(String name, String sqlType) {
        this.name = name;
        this.sqlType = sqlType;
    }

    public String getName() {
        return name;
    }

    public String getSqlType() {
        return sqlType;
    }
}
