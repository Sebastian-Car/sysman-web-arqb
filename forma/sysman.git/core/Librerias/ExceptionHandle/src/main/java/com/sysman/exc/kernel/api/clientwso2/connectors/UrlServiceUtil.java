/*
 * UrlServiceUtil
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.exc.kernel.api.clientwso2.connectors;

import com.sysman.exc.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.exc.kernel.api.clientwso2.util.enums.UrlServiceUtilEnum;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase singleton que contiene un conjunto de parametros del tipo
 * UrlBean que almacenara de forma relativa a URLs de servicios.
 */
import java.util.Map;
import java.util.TreeMap;

public class UrlServiceUtil {

    private static UrlServiceUtil service = new UrlServiceUtil();
    private static Map<String, UrlBean> urls = new TreeMap<>();
    private RequestManager requestManager;

    private UrlServiceUtil() {
        requestManager = new RequestManager();
        urls = new TreeMap<>();
    }

    /**
     * Obtener instancia unica para recuperar informacion e servicios.
     */
    public static UrlServiceUtil getInstance() {
        if (service != null) {
            return service;
        }
        else {
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

    public UrlBean getUrlServiceByUrlByEnumID(String urlEnumId) {
        UrlBean urlBean;
        Map<String, Object> params = new TreeMap();
        params.put("CODIGO", urlEnumId);

        if (!urls.containsKey(urlEnumId)) {
            urlBean = buscarGuardarUrl(
                            UrlServiceUtilEnum.URLGET.getValue(), params);
        }
        else {
            urlBean = urls.get(urlEnumId);
        }

        return urlBean;
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
    private UrlBean buscarGuardarUrl(String urlServicio,
        Map<String, Object> params) {
        UrlBean urlBean = requestManager.getPlainObject(urlServicio, params,
                        UrlBean.class);
        if (urlBean.getCodigo() != null) {
            urls.put(urlBean.getCodigo(), urlBean);
            if (urlBean.getCodigoConteo() != null) {
                params.put("CODIGO", urlBean.getCodigoConteo());
                urlBean.setUrlConteo(buscarGuardarUrl(urlServicio, params));
            }
        }
        else {
            urlBean = null;
        }
        return urlBean;
    }

}
