package com.sysman.kernel.api.clientwso2.dbs;

/**
 * Representa un elemento &lt;resource codigo="..."&gt; de un archivo
 * .dbs, que enlaza un CODIGO (el mismo que usa UrlServiceUtil /
 * GenericUrlEnum: createKey, readKey, updateKey, deleteKey, gridKey)
 * a una query principal por su id, y opcionalmente a una segunda
 * query de conteo (solo para recursos de tipo grilla/combo, cuando
 * se quiere traer el TOTAL integrado sin una segunda consulta).
 */
public class DbsResource {

    private final String codigo;
    private final String queryId;
    private final String queryIdConteo;

    public DbsResource(String codigo, String queryId, String queryIdConteo) {
        this.codigo = codigo == null ? null : codigo.trim();
        this.queryId = queryId;
        this.queryIdConteo = queryIdConteo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getQueryId() {
        return queryId;
    }

    /** Puede ser null: no todo recurso necesita conteo (solo grillas/combos paginados). */
    public String getQueryIdConteo() {
        return queryIdConteo;
    }
}
