/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.beanbase;

import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cmanrique
 */
public abstract class AbstractBeanBaseAcme extends AbstractBeanBase {

    public static final String ACCION_INSERTAR = "i";
    public static final String ACCION_MODIFICAR = "m";
    public static final String ACCION_VER = "v";

    protected String[] llave;

    protected String tabla;
    protected int servicio;
    protected UrlBean urlCreacion;
    protected UrlBean urlLectura;
    protected UrlBean urlActualizacion;
    protected UrlBean urlEliminacion;
    protected UrlBean urlListado;

    protected Map<String, Object> parametrosListado;
    protected GenericUrlEnum enumBase;

    public AbstractBeanBaseAcme() {
        parametrosListado = new HashMap<>();
    }

    public void buscarLlave() {
        try {
            tabla = tabla == null ? enumBase.getTable() : tabla;
            if (tabla != null) {
                llave = CacheUtil.getLlaveServicio(urlConexionCache, tabla);
            }
        }
        catch (SysmanException e) {
            Logger.getLogger(BeanBaseDatosAcme.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void buscarUrls() {
        urlCreacion = enumBase.getCreateKey() != null
            ? UrlServiceUtil.getUrlBeanById(
                            enumBase.getCreateKey())
            : null;

        urlActualizacion = enumBase.getUpdateKey() != null
            ? UrlServiceUtil.getUrlBeanById(
                            enumBase.getUpdateKey())
            : null;

        urlEliminacion = enumBase.getDeleteKey() != null
            ? UrlServiceUtil.getUrlBeanById(
                            enumBase.getDeleteKey())
            : null;

        urlListado = enumBase.getGridKey() != null
            ? UrlServiceUtil.getUrlBeanById(
                            enumBase.getGridKey())
            : null;

    }

    public abstract boolean insertarAntes();

    public abstract boolean insertarDespues();

    public abstract boolean actualizarAntes();

    public abstract boolean actualizarDespues();

    public abstract boolean eliminarAntes();

    public abstract boolean eliminarDespues();

}
