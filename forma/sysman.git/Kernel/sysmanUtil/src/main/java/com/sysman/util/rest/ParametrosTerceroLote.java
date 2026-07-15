/*-
 * ParametrosTerceroLote.java
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
 * Clase que administra los terceros que se envia al servicio
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametrosTerceroLote {

    private String contribuyente;

    private List<ParametrosTercero> terceros;

    /**
     * 
     * @return
     */
    public String getContribuyente() {
        return contribuyente;
    }

    /**
     * 
     * @param contribuyente
     */
    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    /**
     * @return the terceros
     */
    public List<ParametrosTercero> getTerceros() {
        return terceros;
    }

    /**
     * @param terceros
     * the terceros to set
     */
    public void setTerceros(List<ParametrosTercero> terceros) {
        this.terceros = terceros;
    }
}
