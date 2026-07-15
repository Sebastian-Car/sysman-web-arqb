/*-
 * RespuestaFacturasReporte.java
 *
 * 1.0
 * 
 * 21/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Respuesta de cuerpo facturas
 * 
 * @version 1.0, 21/12/2020
 * @author eamaya
 *
 */
public class RespuestaFacturasReporte {

    private String estado;
    private String numFormato;
    private String fecha;
    private String total;
    private String observacion;
    private String tercero;
    private String prefijo;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumFormato() {
        return numFormato;
    }

    public void setNumFormato(String numFormato) {
        this.numFormato = numFormato;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

}
