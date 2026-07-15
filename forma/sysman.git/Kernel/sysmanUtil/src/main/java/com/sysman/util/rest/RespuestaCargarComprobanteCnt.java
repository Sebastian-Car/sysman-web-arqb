/*-
 * RespuestaApi.java
 *
 * 1.0
 * 
 * 26/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase estandar para generar la respuesta de todos los servicios con
 * el fin de enviar los errores de negocio claramente
 * 
 * @version 1.1, 22/01/2020
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaCargarComprobanteCnt {

    String compania;

    String tipo;

    String numero;

    int anio;

    String fechaVcnDoc;

    double vlrDocumento;

    String sucursal;

    String tercero;

    double vlrAGirar;

    String fecha;

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getFechaVcnDoc() {
        return fechaVcnDoc;
    }

    public void setFechaVcnDoc(String fechaVcnDoc) {
        this.fechaVcnDoc = fechaVcnDoc;
    }

    public double getVlrDocumento() {
        return vlrDocumento;
    }

    public void setVlrDocumento(double vlrDocumento) {
        this.vlrDocumento = vlrDocumento;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public double getVlrAGirar() {
        return vlrAGirar;
    }

    public void setVlrAGirar(double vlrAGirar) {
        this.vlrAGirar = vlrAGirar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
