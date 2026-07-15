/*-
 * SalidaInformes2GobNar.java
 *
 * 1.0
 * 
 * 17/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.logica;

import java.io.Serializable;


/**
 * Clase para guardar la salida del servicio SalidaInformesGobNar
 * 
 * @version 1.0, 17/10/2022
 * @author mrosero
 *
 */
public class SalidaInformes2GobNar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String numero;
	private String nombrepred;
	private Object idprede;
	private String nombreplan;
	private Object rubro;
	private String fecha;
	private String tipocpte;
	private String tercero;
	private String nombretercero;
	private String descripcion;
	private String nrodocumento;
	private String valordebito;
	private String valorcredito;
	private String debitoafectado;
	private String creditoafectado;
	private String modificaciondebito;
	private String modificacioncredito;
	private String saldoporejecutaresp;
	private String tipocpteafect;
	private String cmpteafectado;



	/**
	 * @return 
	 * 
	 */
	
	public SalidaInformes2GobNar() {

	}


	public SalidaInformes2GobNar(String numero, String nombrepred, Object idprede, String nombreplan, Object rubro,
			String fecha, String tipocpte, String tercero, String nombretercero, String descripcion,
			String nrodocumento, String valordebito, String valorcredito, String debitoafectado, String creditoafectado,
			String modificaciondebito, String modificacioncredito, String saldoporejecutaresp, String tipocpteafect,
			String cmpteafectado) {

		this.numero = numero;
		this.nombrepred = nombrepred;
		this.idprede = idprede;
		this.nombreplan = nombreplan;
		this.rubro = rubro;
		this.fecha = fecha;
		this.tipocpte = tipocpte;
		this.tercero = tercero;
		this.nombretercero = nombretercero;
		this.descripcion = descripcion;
		this.nrodocumento = nrodocumento;
		this.valordebito = valordebito;
		this.valorcredito = valorcredito;
		this.debitoafectado = debitoafectado;
		this.creditoafectado = creditoafectado;
		this.modificaciondebito = modificaciondebito;
		this.modificacioncredito = modificacioncredito;
		this.saldoporejecutaresp = saldoporejecutaresp;
		this.tipocpteafect = tipocpteafect;
		this.cmpteafectado = cmpteafectado;

	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNombrepred() {
		return nombrepred;
	}

	public void setNombrepred(String nombrepred) {
		this.nombrepred = nombrepred;
	}

	public Object getIdprede() {
		return idprede;
	}

	public void setIdprede(Object idprede) {
		this.idprede = idprede;
	}

	public String getNombreplan() {
		return nombreplan;
	}

	public void setNombreplan(String nombreplan) {
		this.nombreplan = nombreplan;
	}

	public Object getRubro() {
		return rubro;
	}

	public void setRubro(Object rubro) {
		this.rubro = rubro;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTipocpte() {
		return tipocpte;
	}

	public void setTipocpte(String tipocpte) {
		this.tipocpte = tipocpte;
	}

	public String getTercero() {
		return tercero;
	}

	public void setTercero(String tercero) {
		this.tercero = tercero;
	}

	public String getNombretercero() {
		return nombretercero;
	}

	public void setNombretercero(String nombretercero) {
		this.nombretercero = nombretercero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNrodocumento() {
		return nrodocumento;
	}

	public void setNrodocumento(String nrodocumento) {
		this.nrodocumento = nrodocumento;
	}

	public String getValordebito() {
		return valordebito;
	}

	public void setValordebito(String valordebito) {
		this.valordebito = valordebito;
	}

	public String getValorcredito() {
		return valorcredito;
	}

	public void setValorcredito(String valorcredito) {
		this.valorcredito = valorcredito;
	}

	public String getDebitoafectado() {
		return debitoafectado;
	}

	public void setDebitoafectado(String debitoafectado) {
		this.debitoafectado = debitoafectado;
	}

	public String getCreditoafectado() {
		return creditoafectado;
	}

	public void setCreditoafectado(String creditoafectado) {
		this.creditoafectado = creditoafectado;
	}

	public String getModificaciondebito() {
		return modificaciondebito;
	}

	public void setModificaciondebito(String modificaciondebito) {
		this.modificaciondebito = modificaciondebito;
	}

	public String getModificacioncredito() {
		return modificacioncredito;
	}

	public void setModificacioncredito(String modificacioncredito) {
		this.modificacioncredito = modificacioncredito;
	}

	public String getSaldoporejecutaresp() {
		return saldoporejecutaresp;
	}

	public void setSaldoporejecutaresp(String saldoporejecutaresp) {
		this.saldoporejecutaresp = saldoporejecutaresp;
	}

	public String getTipocpteafect() {
		return tipocpteafect;
	}

	public void setTipocpteafect(String tipocpteafect) {
		this.tipocpteafect = tipocpteafect;
	}

	public String getCmpteafectado() {
		return cmpteafectado;
	}

	public void setCmpteafectado(String cmpteafectado) {
		this.cmpteafectado = cmpteafectado;
	}

	
}
