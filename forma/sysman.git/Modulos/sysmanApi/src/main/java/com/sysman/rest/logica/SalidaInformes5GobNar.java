/*-
 * SalidaInformes5GobNar.java
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
public class SalidaInformes5GobNar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String iddeempleado;
	private String apellido1;
	private String apellido2;
	private String nombres;
	private String numerodcto;
	private String expedida;
	private String fechancto;
	private String fechadeingreso;
	private String fechaderetiro;
	private String iddecargo;
	private String nombredelcargo;
	private String iddecategoria;
	private String nombrecategoria;
	private String escalafon;
	private String nombreescalafon;
	private String grado;
	private String decarrera;
	private String salariobaseibc;
	private String dependenciaNombre;
	private String ano;
	private String compania;
	private String emailcorporativo;
	private String emailpersonal;
	private String direccion;
	private String telefonos;
	private String fechacumplimientobonificacion;

	public SalidaInformes5GobNar() {

	}

	/**
	 * @return 
	 * 
	 */
	public SalidaInformes5GobNar(String iddeempleado, String apellido1, String apellido2, String nombres, String numerodcto, String expedida, 
			String fechancto, String fechadeingreso , String fechaderetiro , String iddecargo, String nombredelcargo, String iddecategoria, 
			String nombrecategoria, String escalafon, String nombreescalafon, String grado, String decarrera, String salariobaseibc, String dependenciaNombre, 
			String ano, String compania, String emailcorporativo, String emailpersonal, String direccion, String telefonos, String fechacumplimientobonificacion) {

		this.iddeempleado= iddeempleado;
		this.apellido1= apellido1;
		this.apellido2= apellido2;
		this.nombres= nombres;
		this.numerodcto=  numerodcto ;
		this.expedida=expedida;
		this.fechancto= fechancto;
		this.fechadeingreso=fechadeingreso	;
		this.fechaderetiro=fechaderetiro	;
		this.iddecargo= iddecargo;
		this.nombredelcargo= nombredelcargo;
		this.iddecategoria= iddecategoria;
		this.nombrecategoria= nombrecategoria;
		this.escalafon= escalafon;
		this.nombreescalafon= nombreescalafon;
		this.grado= grado;
		this.decarrera= decarrera;
		this.salariobaseibc= salariobaseibc;
		this.dependenciaNombre= dependenciaNombre;
		this.ano= ano;
		this.compania= compania;
		this.emailcorporativo= emailcorporativo;
		this.emailpersonal= emailpersonal;
		this.direccion= direccion;
		this.telefonos= telefonos;
		this.fechacumplimientobonificacion= fechacumplimientobonificacion;
	}

	public String getIddeempleado() {
		return iddeempleado;
	}

	public void setIddeempleado(String iddeempleado) {
		this.iddeempleado = iddeempleado;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getNumerodcto() {
		return numerodcto;
	}

	public void setNumerodcto(String numerodcto) {
		this.numerodcto = numerodcto;
	}

	public String getExpedida() {
		return expedida;
	}

	public void setExpedida(String expedida) {
		this.expedida = expedida;
	}

	public String getFechancto() {
		return fechancto;
	}

	public void setFechancto(String fechancto) {
		this.fechancto = fechancto;
	}

	public String getFechadeingreso() {
		return fechadeingreso;
	}

	public void setFechadeingreso(String fechadeingreso) {
		this.fechadeingreso = fechadeingreso;
	}

	public String getFechaderetiro() {
		return fechaderetiro;
	}

	public void setFechaderetiro(String fechaderetiro) {
		this.fechaderetiro = fechaderetiro;
	}

	public String getIddecargo() {
		return iddecargo;
	}

	public void setIddecargo(String iddecargo) {
		this.iddecargo = iddecargo;
	}

	public String getNombredelcargo() {
		return nombredelcargo;
	}

	public void setNombredelcargo(String nombredelcargo) {
		this.nombredelcargo = nombredelcargo;
	}

	public String getIddecategoria() {
		return iddecategoria;
	}

	public void setIddecategoria(String iddecategoria) {
		this.iddecategoria = iddecategoria;
	}

	public String getNombrecategoria() {
		return nombrecategoria;
	}

	public void setNombrecategoria(String nombrecategoria) {
		this.nombrecategoria = nombrecategoria;
	}

	public String getEscalafon() {
		return escalafon;
	}

	public void setEscalafon(String escalafon) {
		this.escalafon = escalafon;
	}

	public String getNombreescalafon() {
		return nombreescalafon;
	}

	public void setNombreescalafon(String nombreescalafon) {
		this.nombreescalafon = nombreescalafon;
	}

	public String getGrado() {
		return grado;
	}

	public void setGrado(String grado) {
		this.grado = grado;
	}

	public String getDecarrera() {
		return decarrera;
	}

	public void setDecarrera(String decarrera) {
		this.decarrera = decarrera;
	}

	public String getSalariobaseibc() {
		return salariobaseibc;
	}

	public void setSalariobaseibc(String salariobaseibc) {
		this.salariobaseibc = salariobaseibc;
	}

	public String getDependenciaNombre() {
		return dependenciaNombre;
	}

	public void setDependenciaNombre(String dependenciaNombre) {
		this.dependenciaNombre = dependenciaNombre;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getCompania() {
		return compania;
	}

	public void setCompania(String compania) {
		this.compania = compania;
	}

	public String getEmailcorporativo() {
		return emailcorporativo;
	}

	public void setEmailcorporativo(String emailcorporativo) {
		this.emailcorporativo = emailcorporativo;
	}

	public String getEmailpersonal() {
		return emailpersonal;
	}

	public void setEmailpersonal(String emailpersonal) {
		this.emailpersonal = emailpersonal;
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

	public String getFechacumplimientobonificacion() {
		return fechacumplimientobonificacion;
	}

	public void setFechacumplimientobonificacion(String fechacumplimientobonificacion) {
		this.fechacumplimientobonificacion = fechacumplimientobonificacion;
	}
	
	

}
