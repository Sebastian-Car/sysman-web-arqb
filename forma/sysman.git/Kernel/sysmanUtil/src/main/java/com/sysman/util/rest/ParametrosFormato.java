/*-
 * ParametrosFormato.java
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

/**
 * Clase que sirve como POJO para el nodo de parametros
 * 
 * @version 1.0, 7/01/2021
 * @author eamaya
 *
 */
public class ParametrosFormato {

    private String aliasUsuarioCertificado;
    private String certificado;
    private String nombreCertificado;
    private String numDocumentoContribuyente;
    private String numFormato;
    private String passCertificado;
    private String prefijo;
    private String tipoFormato;
    private boolean muestraCodigoBarras;
    private String codigoBarras;
    private String textoBarras;
    private String tipoAjusteNota;
    private String actividadesEconomicas;

    public String getAliasUsuarioCertificado() {
        return aliasUsuarioCertificado;
    }

    public void setAliasUsuarioCertificado(String aliasUsuarioCertificado) {
        this.aliasUsuarioCertificado = aliasUsuarioCertificado;
    }

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public String getNombreCertificado() {
        return nombreCertificado;
    }

    public void setNombreCertificado(String nombreCertificado) {
        this.nombreCertificado = nombreCertificado;
    }

    public String getNumDocumentoContribuyente() {
        return numDocumentoContribuyente;
    }

    public void setNumDocumentoContribuyente(String numDocumentoContribuyente) {
        this.numDocumentoContribuyente = numDocumentoContribuyente;
    }

    public String getNumFormato() {
        return numFormato;
    }

    public void setNumFormato(String numFormato) {
        this.numFormato = numFormato;
    }

    public String getPassCertificado() {
        return passCertificado;
    }

    public void setPassCertificado(String passCertificado) {
        this.passCertificado = passCertificado;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
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

    public boolean isMuestraCodigoBarras() {
        return muestraCodigoBarras;
    }

    public void setMuestraCodigoBarras(boolean muestraCodigoBarras) {
        this.muestraCodigoBarras = muestraCodigoBarras;
    }

	public String getTipoAjusteNota() {
		return tipoAjusteNota;
	}

	public void setTipoAjusteNota(String tipoAjusteNota) {
		this.tipoAjusteNota = tipoAjusteNota;
	}

	public String getActividadesEconomicas() {
		return actividadesEconomicas;
	}

	public void setActividadesEconomicas(String actividadesEconomicas) {
		this.actividadesEconomicas = actividadesEconomicas;
	}

}
