/*-
 * ParametrosReenviarCorreoFactura.java
 *
 * 1.0
 * 
 * 28/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que sirve como POJO para el servicio de Reenviar correo factura
 * 
 * @version 1.0, 28/05/2024
 * @author jmillan
 *
 */
public class ParametrosReenviarCorreoFactura {
    private String certificado;
    private String nombreCertificado;
    private String numDocumentoContribuyente;
    private String numFormato;
    private String passCertificado;
    private String prefijo;
    private String tipoFormato;
    private boolean muestraCodigoBarras;
    private String idFactura;
    private String codigoReporte;
    private String actividadesEconomicas;
    
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
    
    public boolean isMuestraCodigoBarras(){
    	return this.muestraCodigoBarras;
    }

    public void setMuestraCodigoBarras(boolean muestraCodigoBarras){
    	this.muestraCodigoBarras = muestraCodigoBarras;
    }

	public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }
    
	public String getCodigoReporte() {
        return codigoReporte;
    }

    public void setCodigoReporte(String codigoReporte) {
        this.codigoReporte = codigoReporte;
    }

	public String getActividadesEconomicas() {
		return actividadesEconomicas;
	}

	public void setActividadesEconomicas(String actividadesEconomicas) {
		this.actividadesEconomicas = actividadesEconomicas;
	}
}
