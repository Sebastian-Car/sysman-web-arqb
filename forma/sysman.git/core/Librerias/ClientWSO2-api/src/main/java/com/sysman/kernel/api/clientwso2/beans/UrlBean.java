/*
 * UrlBean
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */

package com.sysman.kernel.api.clientwso2.beans;

import java.io.Serializable;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Bean para almacenar informacion de servicios (Código,
 * nombre y URL).
 */
public class UrlBean implements Serializable {

    private String codigo;
    private String url;
    private String nombre;
    private String metodo;
    private String codigoConteo;
    private UrlBean urlConteo;

    public UrlBean() {
        super();
    }

    public UrlBean(String codigo, String url, String nombre, String metodo) {
        super();
        this.codigo = codigo;
        this.url = url;
        this.nombre = nombre;
        this.metodo = metodo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre
     * the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the urlConteo
     */
    public UrlBean getUrlConteo() {
        return urlConteo;
    }

    /**
     * @param urlConteo
     * the urlConteo to set
     */
    public void setUrlConteo(UrlBean urlConteo) {
        this.urlConteo = urlConteo;
    }

    /**
     * @return the metodo
     */
    public String getMetodo() {
        return metodo;
    }

    /**
     * @param metodo
     * the metodo to set
     */
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    /**
     * @return the codigoConteo
     */
    public String getCodigoConteo() {
        return codigoConteo;
    }

    /**
     * @param codigoConteo
     * the codigoConteo to set
     */
    public void setCodigoConteo(String codigoConteo) {
        this.codigoConteo = codigoConteo;
    }

}
