/*-
 * ParametrosLegalizarFactura.java
 *
 * 1.0
 * 
 * 7/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * Clase que sirve como POJO para el servicio de legalizar factura
 * 
 * @version 1.0, 7/01/2021
 * @author eamaya
 *
 */
public class ParametrosLegalizarFactura {
    private String prefijo;
    private String testSetId;
    private int tipoReporte;
    private int tipoSalida;
    private String codigoReporte;
    private ParametrosFormato paramFormato;
    private List<ParamAdjuntos> adjuntos;

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getTestSetId() {
        return testSetId;
    }

    public void setTestSetId(String testSetId) {
        this.testSetId = testSetId;
    }

    public int getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(int tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public int getTipoSalida() {
        return tipoSalida;
    }

    public void setTipoSalida(int tipoSalida) {
        this.tipoSalida = tipoSalida;
    }

    public String getCodigoReporte() {
        return codigoReporte;
    }

    public void setCodigoReporte(String codigoReporte) {
        this.codigoReporte = codigoReporte;
    }

    public ParametrosFormato getParamFormato() {
        return paramFormato;
    }

    public void setParamFormato(ParametrosFormato paramFormato) {
        this.paramFormato = paramFormato;
    }

    public List<ParamAdjuntos> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<ParamAdjuntos> adjuntos) {
        this.adjuntos = adjuntos;
    }

}
