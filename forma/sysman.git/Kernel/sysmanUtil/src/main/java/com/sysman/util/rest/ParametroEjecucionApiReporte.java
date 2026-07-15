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
public class ParametroEjecucionApiReporte {

    private String codigoReporte;

    private String compania;

    private String entidad;

    private String formatoReporte;

    private String idioma;

    private String url;

    private boolean usaAdaptador;

    private ParametroCuerpoEjecucionReporte paramReporte;

    public String getCodigoReporte() {
        return codigoReporte;
    }

    public void setCodigoReporte(String codigoReporte) {
        this.codigoReporte = codigoReporte;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getFormatoReporte() {
        return formatoReporte;
    }

    public void setFormatoReporte(String formatoReporte) {
        this.formatoReporte = formatoReporte;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsaAdaptador() {
        return usaAdaptador;
    }

    public void setUsaAdaptador(boolean usaAdaptador) {
        this.usaAdaptador = usaAdaptador;
    }

    public ParametroCuerpoEjecucionReporte getParamReporte() {
        return paramReporte;
    }

    public void setParamReporte(ParametroCuerpoEjecucionReporte paramReporte) {
        this.paramReporte = paramReporte;
    }

}
