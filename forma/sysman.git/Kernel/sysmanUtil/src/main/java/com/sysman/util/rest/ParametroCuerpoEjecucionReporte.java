/*-
 * ParametroCuerpoEnvioFactura.java
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

/**
 * Clase que maneja los parametros del cuerpo de factura
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametroCuerpoEjecucionReporte {

    private String numFactura;

    private String numContribuyente;

    private String tipoFormato;

    private String prefijo;

    private String codigoBarras;

    private String textoBarras;
    
    private String originalycopia;
    
    private String textoPieDeFactura;
    
    private String actividadesEconomicas;

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public String getNumContribuyente() {
        return numContribuyente;
    }

    public void setNumContribuyente(String numContribuyente) {
        this.numContribuyente = numContribuyente;
    }

    public String getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getTextoBarras() {
        return textoBarras;
    }

    public void setTextoBarras(String textoBarras) {
        this.textoBarras = textoBarras;
    }

	public String getOriginalycopia() {
		return originalycopia;
	}

	public void setOriginalycopia(String originalycopia) {
		this.originalycopia = originalycopia;
	}

	public String getTextoPieDeFactura() {
		return textoPieDeFactura;
	}

	public void setTextoPieDeFactura(String textoPieDeFactura) {
		this.textoPieDeFactura = textoPieDeFactura;
	}

	public String getActividadesEconomicas() {
		return actividadesEconomicas;
	}

	public void setActividadesEconomicas(String actividadesEconomicas) {
		this.actividadesEconomicas = actividadesEconomicas;
	}

}
