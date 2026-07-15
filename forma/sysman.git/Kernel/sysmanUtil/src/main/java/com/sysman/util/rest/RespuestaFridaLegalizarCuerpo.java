/*-
 * RespuestaFridaLegalizarCuerpo.java
 *
 * 1.0
 * 
 * 8/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * cuerpo de la respuesta al servicio de legalizar factura
 * 
 * @version 1.0, 8/01/2021
 * @author eamaya
 *
 */
public class RespuestaFridaLegalizarCuerpo {

    private String zip;
    private String Reporte;
    private RespuestaFridaLegalizarCuerpoDian resultadoProcesoDian;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getReporte() {
        return Reporte;
    }

    public void setReporte(String reporte) {
        Reporte = reporte;
    }

    public RespuestaFridaLegalizarCuerpoDian getResultadoProcesoDian() {
        return resultadoProcesoDian;
    }

    public void setResultadoProcesoDian(
        RespuestaFridaLegalizarCuerpoDian resultadoProcesoDian) {
        this.resultadoProcesoDian = resultadoProcesoDian;
    }

}
