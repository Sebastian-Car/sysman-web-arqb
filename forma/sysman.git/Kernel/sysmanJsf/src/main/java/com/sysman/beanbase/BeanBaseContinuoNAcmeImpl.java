/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.beanbase;

import com.sysman.dao.Registro;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cmanrique
 */
public abstract class BeanBaseContinuoNAcmeImpl extends AbstractBeanBase {

    protected String origenDatos;
    protected String tabla;
    protected boolean cargado;
    protected Registro registro;
    protected RegistroDataModelImpl listaInicial;
    protected UrlBean urlListado;
    protected Map<String, Object> parametrosListado;

    public BeanBaseContinuoNAcmeImpl() {
        parametrosListado = new HashMap<>();
    }

    public void cargarForma() {
        if (!cargado) {
            listaInicial = new RegistroDataModelImpl(null, parametrosListado);
            listaInicial.setUrl(urlListado.getUrl());
            listaInicial.setUrlConteo(urlListado.getUrlConteo().getUrl());
            cargado = true;
        }
    }

    public String getOrigenDatos() {
        return origenDatos;
    }

    public void setOrigenDatos(String origenDatos) {
        this.origenDatos = origenDatos;
    }

    public boolean isCargado() {
        return cargado;
    }

    public void setCargado(boolean cargado) {
        this.cargado = cargado;
    }

    public RegistroDataModelImpl getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(RegistroDataModelImpl listaInicial) {
        this.listaInicial = listaInicial;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

}
