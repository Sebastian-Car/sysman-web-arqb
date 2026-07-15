package com.sysman.services;

import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class RegistroDataModelImpl extends LazyDataModel<Registro> {

    private static final String INDICADOR_DE_SELECCIONADO = "SELECCIONADO_EN_PARTE_GRAFICA";
    private static final String PAGINICIO = "PAGINICIO";
    private static final String PAGTAMANIO = "PAGTAMANIO";

    private transient RequestManager requestManager;
    private transient Map<String, Object> params;
    private transient List<Registro> datasource;

    private String url;
    private String urlConteo;
    private String[] nombreLlave;
    private transient Map<String, Object> filters;
    private String rowKey;
    private boolean vacio;
    private int actual;

    private boolean multiple;

    private transient Registro registroVacio;
    private transient List<Registro> seleccionados;
    private transient List<Registro> filtradoMultiple;
    private transient List<Registro> seleccionadosAux;

    private transient List<String> llavesSeleccionadas;

    private final transient Log logger = LogFactory.getLog(this.getClass());
    
    private boolean incluirTodos;

    /**
     * Constructor por defecto
     */
    public RegistroDataModelImpl() {
        requestManager = new RequestManager();
        datasource = new ArrayList<>();
        params = new HashMap<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para grillas de formularios
     *
     * @param nombreLlave
     * @param params
     */
    public RegistroDataModelImpl(String[] nombreLlave,
        Map<String, Object> params) {
        this.nombreLlave = nombreLlave;
        this.params = params;
        datasource = new ArrayList<>();
        requestManager = new RequestManager();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para subformularios con filtros, busqueda por
     * rowkey
     *
     * @param url
     * @param urlConteo
     * @param params
     * @param vacio
     * @param rowKey
     */
    public RegistroDataModelImpl(String url,
        String urlConteo, Map<String, Object> params,
        String rowKey) {

        this.url = url;
        this.urlConteo = urlConteo;
        this.params = params;
        this.rowKey = rowKey;
        requestManager = new RequestManager();
        datasource = new ArrayList<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para subformularios con filtros, busqueda por llave
     *
     * @param url
     * @param urlConteo
     * @param params
     * @param vacio
     * @param rowKey
     */
    public RegistroDataModelImpl(String url,
        String urlConteo, Map<String, Object> params,
        String[] nombreLlave) {
        this.url = url;
        this.urlConteo = urlConteo;
        this.params = params;
        this.nombreLlave = nombreLlave;
        requestManager = new RequestManager();
        datasource = new ArrayList<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para combos grandes
     *
     * @param url
     * @param urlConteo
     * @param params
     * @param vacio
     * @param rowKey
     */
    public RegistroDataModelImpl(String url,
        String urlConteo, Map<String, Object> params, boolean vacio,
        String rowKey) {
        this.url = url;
        this.urlConteo = urlConteo;
        this.params = params;
        this.vacio = vacio;
        this.rowKey = rowKey;
        requestManager = new RequestManager();
        datasource = new ArrayList<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para combos grandes busqueda por llave
     *
     * @param url
     * @param urlConteo
     * @param params
     * @param vacio
     * @param rowKey
     */
    public RegistroDataModelImpl(String url,
        String urlConteo, Map<String, Object> params, boolean vacio,
        String[] nombreLlave) {
        this.url = url;
        this.urlConteo = urlConteo;
        this.params = params;
        this.vacio = vacio;
        this.nombreLlave = nombreLlave;
        requestManager = new RequestManager();
        datasource = new ArrayList<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para listas multiples donde se hace busqueda por
     * rowkey
     *
     * @param url
     * @param urlConteo
     * @param params
     * @param vacio
     * @param rowKey
     * @param multiple
     */
    public RegistroDataModelImpl(String url,
        String urlConteo, Map<String, Object> params,
        boolean vacio, String rowKey,
        boolean multiple) {
        this.url = url;
        this.urlConteo = urlConteo;
        this.params = params;
        this.vacio = vacio;
        this.rowKey = rowKey;
        this.multiple = multiple;
        requestManager = new RequestManager();
        datasource = new ArrayList<>();
        seleccionados = new ArrayList<>();
        llavesSeleccionadas = new ArrayList<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    /**
     * Constructor para listas multiples donde se hace busqueda por
     * nombres de llaves
     *
     * @param url
     * @param urlConteo
     * @param params
     * @param vacio
     * @param nombreLlave
     * @param multiple
     */
    public RegistroDataModelImpl(String url,
        String urlConteo, Map<String, Object> params,
        boolean vacio, String[] nombreLlave,
        boolean multiple) {
        this.url = url;
        this.urlConteo = urlConteo;
        this.params = params;
        this.vacio = vacio;
        this.nombreLlave = nombreLlave;
        this.multiple = multiple;
        datasource = new ArrayList<>();
        requestManager = new RequestManager();
        seleccionados = new ArrayList<>();
        llavesSeleccionadas = new ArrayList<>();
        registroVacio = new Registro(new HashMap<String, Object>());
    }

    @Override
    public List<Registro> load(int first, int pageSize, String sortField,
        SortOrder sortOrder,
        Map<String, Object> filters) {

        datasource.clear();
        if (url != null) {
            Map<String, Object> parametros;
            if (params != null) {
                parametros = new HashMap<>(params);
            }
            else {
                parametros = new HashMap<>();
            }
            int inicio = !vacio || (first == 0) ? first : (first / 11) * 10;
            int tamPagina = !vacio ? pageSize : 10;
            parametros.put(PAGINICIO, String.valueOf(inicio));
            parametros.put(PAGTAMANIO, String.valueOf(tamPagina));

            this.filters = filters;
            this.actual = first;

            recorrerParametros(parametros);
            try {
                List<Parameter> list = requestManager.getList(url, parametros);
                Parameter tem = requestManager.get(urlConteo, parametros);

                int total = (Integer) tem.getFields().get("TOTAL");
                total = vacio
                                ? ((total / 10) + ((total % 10) == 0 ? 0 : 1)) * 11
                                    : total;

                                int indice = first;
                                if (vacio) {
                                    registroVacio.setIndice(indice);
                                    datasource.add(registroVacio);
                                }
                                if (incluirTodos && actual == 0) {
                                    Registro todos = new Registro();
                                    todos.getCampos().put("CODIGO", 0);
                                    todos.getCampos().put("DESCRIPCION", "TODOS");
                                    datasource.add(vacio ? 1 : 0, todos);
                                }
                                for (Parameter l : list) {
                                    Registro r = new Registro(indice, l.getFields());
                                    r.asignarLlave(nombreLlave);
                                    if (multiple) {
                                        seleccionarParteGrafica(r);
                                    }
                                    datasource.add(r);
                                    indice++;
                                }

                                this.setRowCount(total);
                                parametros.clear();
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                this.setRowCount(0);
            }
        }

        return datasource;

    }

    private void recorrerParametros(Map<String, Object> parametros) {
        if(filters!=null && !filters.isEmpty()){
            java.util.Set<String> keys = filters.keySet();

            for (String key : keys) {
                parametros.put(key
                                .substring(key.indexOf("['") + 2,
                                                key.indexOf("']")),
                                filters.get(key));
            }
        }
    }

    public void load(int first) {
        load(first, getPageSize(), null, null, filters);
    }
    
    public void load(int first,int pageSize) {
        load(first, pageSize, null, null, filters);
    }

    public void load() {
        load(actual);
    }

    @Override
    public Registro getRowData(String rowKey) {
        if (nombreLlave != null) {
            return buscarPorLlave(rowKey);
        }
        else if (rowKey != null) {
            return buscarPorColumna(rowKey);
        }
        return null;
    }

    private Registro buscarPorLlave(String rowKey) {
        for (Registro reg : datasource) {
            if (reg.getLlave().toString().equals(rowKey)) {

                if (multiple && (this.rowKey != null)
                                && !llavesSeleccionadas.contains(
                                                reg.getCampos().get(this.rowKey)
                                                .toString())) {
                    seleccionados.add(reg);
                    llavesSeleccionadas.add(reg.getCampos().get(this.rowKey)
                                    .toString());
                    reg.getCampos().put(INDICADOR_DE_SELECCIONADO, true);
                }
                else if (multiple && (this.nombreLlave != null)
                                && !llavesSeleccionadas.contains(
                                                reg.getLlave().toString())) {
                    seleccionados.add(reg);
                    llavesSeleccionadas.add(reg.getLlave()
                                    .toString());
                    reg.getCampos().put(INDICADOR_DE_SELECCIONADO, true);
                }

                return reg;

            }
        }
        return null;
    }

    private Registro buscarPorColumna(String rowKey) {
        for (Registro reg : datasource) {
            if (!reg.getCampos().isEmpty()
                            && reg.getCampos().get(this.rowKey).toString().equals(rowKey)) {
                if (multiple && !llavesSeleccionadas.contains(
                                reg.getCampos().get(this.rowKey).toString())) {
                    seleccionados.add(reg);
                    llavesSeleccionadas.add(reg.getCampos().get(this.rowKey)
                                    .toString());
                    reg.getCampos().put(INDICADOR_DE_SELECCIONADO, true);
                }
                return reg;
            }
        }
        return new Registro(new HashMap<String, Object>());
    }

    @Override
    public Object getRowKey(Registro object) {
        if (nombreLlave != null) {
            return object.getLlave();
        }
        else if (rowKey != null) {
            return object.getCampos().get(this.rowKey);
        }
        return null;
    }

    public void seleccionarParteGrafica(Registro regTemp) {
        if (nombreLlave != null) {
            regTemp.getCampos().put(INDICADOR_DE_SELECCIONADO,
                            llavesSeleccionadas.contains(
                                            regTemp.getLlave()
                                            .toString()));
        }
        else if (rowKey != null) {
            regTemp.getCampos().put(INDICADOR_DE_SELECCIONADO,
                            llavesSeleccionadas.contains(regTemp.getCampos()
                                            .get(this.rowKey)
                                            .toString()));
        }

    }

    public void deselecionar(Registro reg) {
        reg.getCampos().put(INDICADOR_DE_SELECCIONADO, false);
        llavesSeleccionadas.remove(reg.getLlave().toString());
        this.seleccionados.remove(reg);
    }

    public void setSeleccionados(Map<String, Object> parametros) {
        llavesSeleccionadas.clear();
        seleccionados.clear();

        if (parametros == null) {
            return;
        }
        parametros.putAll(params);
        try {
            Parameter tem = requestManager.get(urlConteo, parametros);
            int total = (Integer) tem.getFields().get("TOTAL");
            parametros.put(PAGINICIO, "0");
            parametros.put(PAGTAMANIO, total);
            List<Parameter> list = requestManager.getList(url,
                            parametros);
            for (Parameter l : list) {
                Registro r = new Registro();
                r.setCampos(l.getFields());
                r.asignarLlave(nombreLlave);
                r.getCampos().put(
                                INDICADOR_DE_SELECCIONADO,
                                true);

                seleccionados.add(r);

                if (nombreLlave != null) {

                    llavesSeleccionadas.add(r.getLlave().toString());
                }
                else if (rowKey != null) {
                    llavesSeleccionadas.add(r.getCampos()
                                    .get(this.rowKey).toString());
                }
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void setSeleccionados(Map<String, Object> parametros,
        String urlSeleccionados) {
        llavesSeleccionadas.clear();
        seleccionados.clear();

        if (parametros == null) {
            return;
        }
        try {
            List<Parameter> list = requestManager.getList(urlSeleccionados,
                            parametros);
            for (Parameter l : list) {
                Registro r = new Registro();
                r.setCampos(l.getFields());
                r.asignarLlave(nombreLlave);
                r.getCampos().put(
                                INDICADOR_DE_SELECCIONADO,
                                true);

                seleccionados.add(r);

                if (nombreLlave != null) {

                    llavesSeleccionadas.add(r.getLlave().toString());
                }
                else if (rowKey != null) {
                    llavesSeleccionadas.add(r.getCampos()
                                    .get(this.rowKey).toString());
                }
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public Registro getRegistroUnico(Map<String, Object> params)
                    throws SystemException {
        Registro rta = null;
        if (params != null) {
            if(this.params!=null){
                params.putAll(this.params);
            }
            params.put(PAGINICIO, "0");
            params.put(PAGTAMANIO, "1");
            Parameter par = requestManager.get(url, params);
            rta = RegistroConverter.toRegistro(par, nombreLlave);
        }
        return rta;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the urlConteo
     */
    public String getUrlConteo() {
        return urlConteo;
    }

    /**
     * @param urlConteo
     * the urlConteo to set
     */
    public void setUrlConteo(String urlConteo) {
        this.urlConteo = urlConteo;
    }

    /**
     * @return the datasource
     */
    public List<Registro> getDatasource() {
        return datasource;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @param params
     * the params to set
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * @return the vacio
     */
    public boolean isVacio() {
        return vacio;
    }

    /**
     * @param vacio
     * the vacio to set
     */
    public void setVacio(boolean vacio) {
        this.vacio = vacio;
    }

    /**
     * @return the seleccionados
     */
    public List<Registro> getSeleccionados() {
        return seleccionados;
    }

    /**
     * @param seleccionados
     * the seleccionados to set
     */
    public void setSeleccionados(List<Registro> seleccionados) {
        this.seleccionados = seleccionados;
    }

    /**
     * @return the seleccionadosAux
     */
    public List<Registro> getSeleccionadosAux() {
        return seleccionadosAux;
    }

    /**
     * @param seleccionadosAux
     * the seleccionadosAux to set
     */
    public void setSeleccionadosAux(List<Registro> seleccionadosAux) {
        this.seleccionadosAux = seleccionadosAux;
    }

    /**
     * @return the filtradoMultiple
     */
    public List<Registro> getFiltradoMultiple() {
        return filtradoMultiple;
    }

    /**
     * @param filtradoMultiple
     * the filtradoMultiple to set
     */
    public void setFiltradoMultiple(List<Registro> filtradoMultiple) {
        this.filtradoMultiple = filtradoMultiple;
    }

    /**
     * @return the llavesSeleccionadas
     */
    public List<String> getLlavesSeleccionadas() {
        return llavesSeleccionadas;
    }

    public boolean isIncluirTodos() {
		return incluirTodos;
	}

	public void setIncluirTodos(boolean incluirTodos) {
		this.incluirTodos = incluirTodos;
	}

}
