package com.sysman.util.rest;

/*-
 * RespuestaApi.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @version 1.1, 21/12/2020
 * @author lvega
 *
 */
@XmlRootElement
public class ParametrosConsultarNomina {

	String codigoTipoNomina;
	String fechaGenNie008;
	String numeroDocumentoNie045;
	String numero;
	String nitEmpleador;
	String usuarioAccion;
	String passCert;
	String testID;
	String nombreCertificado;
	String certBase64;
	public String getCodigoTipoNomina() {
		return codigoTipoNomina;
	}
	public void setCodigoTipoNomina(String codigoTipoNomina) {
		this.codigoTipoNomina = codigoTipoNomina;
	}
	public String getFechaGenNie008() {
		return fechaGenNie008;
	}
	public void setFechaGenNie008(String fechaGenNie008) {
		this.fechaGenNie008 = fechaGenNie008;
	}
	public String getNumeroDocumentoNie045() {
		return numeroDocumentoNie045;
	}
	public void setNumeroDocumentoNie045(String numeroDocumentoNie045) {
		this.numeroDocumentoNie045 = numeroDocumentoNie045;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getNitEmpleador() {
		return nitEmpleador;
	}
	public void setNitEmpleador(String nitEmpleador) {
		this.nitEmpleador = nitEmpleador;
	}
	public String getUsuarioAccion() {
		return usuarioAccion;
	}
	public void setUsuarioAccion(String usuarioAccion) {
		this.usuarioAccion = usuarioAccion;
	}
	public String getPassCert() {
		return passCert;
	}
	public void setPassCert(String passCert) {
		this.passCert = passCert;
	}
	public String getTestID() {
		return testID;
	}
	public void setTestID(String testID) {
		this.testID = testID;
	}
	public String getNombreCertificado() {
		return nombreCertificado;
	}
	public void setNombreCertificado(String nombreCertificado) {
		this.nombreCertificado = nombreCertificado;
	}
	public String getCertBase64() {
		return certBase64;
	}
	public void setCertBase64(String certBase64) {
		this.certBase64 = certBase64;
	}
	
	
}
