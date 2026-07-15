package com.sysman.util.rest;

import java.math.BigInteger;

public class ParametrosXmlCrear {
	private String certificado;
	
	private String contribuyente;
	
	private String nombreCertificado;
	
	private String numFormato;
	
	private String passCertificado;
	
	private String prefijo;
	
	public String getCertificado() {
		return certificado;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

	public String getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(String nitCompania) {
		this.contribuyente = nitCompania;
	}

	public String getNombreCertificado() {
		return nombreCertificado;
	}

	public void setNombreCertificado(String nombreCertificado) {
		this.nombreCertificado = nombreCertificado;
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

	public String getTipodocumento() {
		return tipodocumento;
	}

	public void setTipodocumento(String tipodocumento) {
		this.tipodocumento = tipodocumento;
	}

	private String tipodocumento;
	
}
