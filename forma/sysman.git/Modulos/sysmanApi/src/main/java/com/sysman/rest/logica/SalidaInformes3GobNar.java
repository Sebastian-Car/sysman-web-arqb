/*-
 * SalidaInformes3GobNar.java
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
public class SalidaInformes3GobNar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nit;
	private String terceronombre;
	private String direccion;
	private String nombreciudad;
	private String departamento;
	private String telefonos;
	private String fax;
	private String direccionemail;
	private String clase;
	private String banco;
	private String cuenta;
	private String tipocuenta;
	private String activa;

	/**
	 * @return
	 * 
	 */
	public SalidaInformes3GobNar() {

	}

	public SalidaInformes3GobNar(String nit, String terceronombre, String direccion, String nombreciudad,
			String departamento, String telefonos, String fax, String direccionemail, String clase, String banco,
			String cuenta, String tipocuenta, String activa) {

		this.nit = nit;
		this.terceronombre = terceronombre;
		this.direccion = direccion;
		this.nombreciudad = nombreciudad;
		this.departamento = departamento;
		this.telefonos = telefonos;
		this.fax = fax;
		this.direccionemail = direccionemail;
		this.clase = clase;
		this.banco = banco;
		this.cuenta = cuenta;
		this.tipocuenta = tipocuenta;
		this.activa = activa;

	}

	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	public String getTerceronombre() {
		return terceronombre;
	}

	public void setTerceronombre(String terceronombre) {
		this.terceronombre = terceronombre;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getTipocuenta() {
		return tipocuenta;
	}

	public void setTipocuenta(String tipocuenta) {
		this.tipocuenta = tipocuenta;
	}

	public String getActiva() {
		return activa;
	}

	public void setActiva(String activa) {
		this.activa = activa;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getNombreciudad() {
		return nombreciudad;
	}

	public void setNombreciudad(String nombreciudad) {
		this.nombreciudad = nombreciudad;
	}

	public String getDireccionemail() {
		return direccionemail;
	}

	public void setDireccionemail(String direccionemail) {
		this.direccionemail = direccionemail;
	}

}
