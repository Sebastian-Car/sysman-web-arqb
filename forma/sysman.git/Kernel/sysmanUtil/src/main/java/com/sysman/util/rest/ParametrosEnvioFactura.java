/*-
 * ParametrosEnvioFactura.java
 *
 * 1.0
 * 
 * 4/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * lase que administra los parametros del servicio
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametrosEnvioFactura {

    private String createdBy;

    private String numerocontribuyente;

    private List<ParametroCuerpoEnvioFactura> facturas;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getNumerocontribuyente() {
        return numerocontribuyente;
    }

    public void setNumerocontribuyente(String numerocontribuyente) {
        this.numerocontribuyente = numerocontribuyente;
    }

    public List<ParametroCuerpoEnvioFactura> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<ParametroCuerpoEnvioFactura> facturas) {
        this.facturas = facturas;
    }

}
