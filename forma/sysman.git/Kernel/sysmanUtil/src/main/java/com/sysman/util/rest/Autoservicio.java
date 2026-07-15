/*-
 * Autoservicio.java
 *
 * 1.0
 * 
 * 25/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que permite el manejo del objeto autoservico con sus
 * atributos de inicio de sesion, para el caso del login.
 * 
 * @version 1.0
 * @author José Pascual Gómez Blanco
 * @fecha 17/09/2018
 *
 */
public class Autoservicio {
    /**
     * entidad desde la cual se genera el autoservicio
     */
    private String entidad;
    private String compania;
    private int clase;
    private String cedula;
    private int proceso;
    private int ano;
    private int mes;
    private int periodo;
    private String observacion;
    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public int getClase() {
        return clase;
    }

    public void setClase(int clase) {
        this.clase = clase;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getProceso() {
        return proceso;
    }

    public void setProceso(int proceso) {
        this.proceso = proceso;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }


}
