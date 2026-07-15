/*-
 * RespuestaNotasReporte.java
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
 * Respuesta de cuerpo facturas notas
 * 
 * @version 1.0, 21/12/2020
 * @author eamaya
 *
 */
public class RespuestaNotasReporte {
    private String clase;
    private String estado;
    private String fecha;
    private String numFactura;
    private String numFormato;
    private String observacion;
    private String tipoNota;

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public String getNumFormato() {
        return numFormato;
    }

    public void setNumFormato(String numFormato) {
        this.numFormato = numFormato;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(String tipoNota) {
        this.tipoNota = tipoNota;
    }

}
