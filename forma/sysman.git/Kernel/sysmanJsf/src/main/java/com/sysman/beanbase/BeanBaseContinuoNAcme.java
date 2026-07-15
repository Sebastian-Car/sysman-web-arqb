/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.beanbase;

import com.sysman.dao.Registro;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

/**
 *
 * @author cmanrique
 */
public abstract class BeanBaseContinuoNAcme extends AbstractBeanBase {

    protected String origenDatos;
    protected String tabla;
    protected boolean cargado;
    protected RegistroDataModel listaInicial;
    protected Registro registro;
    protected String nombreConexion;
    protected ConectorPool conectorPool;

    public BeanBaseContinuoNAcme() {
        nombreConexion = ConectorPool.ESQUEMA_SYSMAN;
        conectorPool = new ConectorPool();
    }

    public void cargarForma() {
        if (!cargado) {
            listaInicial = new RegistroDataModel(nombreConexion,
                            ":FRFR" + numFormulario + ":TBFR" + numFormulario,
                            origenDatos);
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

    public RegistroDataModel getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(RegistroDataModel listaInicial) {
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
