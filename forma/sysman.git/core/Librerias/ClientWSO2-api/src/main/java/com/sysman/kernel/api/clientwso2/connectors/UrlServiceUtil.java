/*
 * UrlServiceUtil
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.api.clientwso2.connectors;

import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.dbs.DbsDispatcherConfig;
import com.sysman.kernel.api.clientwso2.util.enums.UrlServiceUtilEnum;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase singleton que contiene un conjunto de parametros del tipo
 * UrlBean que almacenara de forma relativa a URLs de servicios.
 */
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class UrlServiceUtil {

    private static UrlServiceUtil service = new UrlServiceUtil();
    private static Map<String, UrlBean> urls = new TreeMap<>();
    private RequestManager requestManager;
    private String urlServiceRecursos;

    private UrlServiceUtil() {
        requestManager = new RequestManager();
        urls = new TreeMap<>();
        ResourceBundle config = ResourceBundle.getBundle("config");
        urlServiceRecursos = config
                        .getString(UrlServiceUtilEnum.URL_RECURSOS.getValue());
    }

    /**
     * Obtener instancia unica para recuperar informacion e servicios.
     */
    public static UrlServiceUtil getInstance() {
        if (service != null) {
            return service;
        } else {
            return new UrlServiceUtil();
        }
    }

    /**
     * Obtener listado de objetos URLBean con informacion de servicios
     * y sus respectivas URLs.
     */
    public Map<String, UrlBean> getUrlBeans() {
        return urls;
    }

    /**
     * Este metodo busca el objeto UrlBean cuyo ID se ingresa por
     * parametro, validado si ya se encuentra almacenado en el map
     * urls, e tal caso retorna dicho objeto que esta almacenado. Enel
     * caso contrario realiza la busqueda del objeto por medio del
     * servicio correspondiete.
     * 
     * @param urlEnumId
     * Id del objeto URL que se debe buscar
     * @return Objeto UrlBean con el id ingresado por parametro
     */
    public UrlBean getUrlServiceByUrlByEnumID(String urlEnumId) {
        if (urls.containsKey(urlEnumId)) {
            return urls.get(urlEnumId);
        }

        // Si el CODIGO esta migrado a local (dbs-switch.properties),
        // no se consulta WSO2 en absoluto: se arma un UrlBean
        // sintetico con un prefijo artificial que RequestManager
        // reconoce y despacha localmente contra el .dbs.
        if (DbsDispatcherConfig.getInstance().esLocal(urlEnumId)) {
            UrlBean urlBean = new UrlBean(urlEnumId,
                            DbsDispatcherConfig.PREFIJO_LOCAL + urlEnumId,
                            urlEnumId, null);
            urls.put(urlEnumId, urlBean);
            return urlBean;
        }

        Map<String, Object> params = new TreeMap<>();
        params.put("CODIGO", urlEnumId);
        return buscarGuardarUrl(params);
    }

    /**
     * Consume el servicio ingresado por parametro y segun la
     * respuesta crea un objeto de la clase UrlBean y lo almacena en
     * el maps de urls
     * 
     * @param urlServicio
     * URL dede donde se deben obtener los datos del recurso
     * @param params
     * Map de parametros necesarios para consumir el servicio
     * @return Objeto de la clase UrlBean con los datos del recurso
     * solicitado o null si no encuentra un recurso correspondiente
     */
    private UrlBean buscarGuardarUrl(Map<String, Object> params) {
        UrlBean urlBean = requestManager.getPlainObject(urlServiceRecursos,
                        params,
                        UrlBean.class);
        if (urlBean != null) {
            if (urlBean.getCodigo() != null) {
                urls.put(urlBean.getCodigo(), urlBean);
                if (urlBean.getCodigoConteo() != null) {
                    params.put("CODIGO", urlBean.getCodigoConteo());
                    urlBean.setUrlConteo(
                                    buscarGuardarUrl(params));
                }
            } else {
                urlBean = null;
            }
        }
        return urlBean;
    }

    public static UrlBean getUrlBeanById(String urlEnumId) {

        return getInstance().getUrlServiceByUrlByEnumID(
                        urlEnumId);
    }

}
