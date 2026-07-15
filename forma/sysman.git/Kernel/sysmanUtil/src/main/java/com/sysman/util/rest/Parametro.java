/*-
 * Parametro.java
 *
 * 1.0
 * 
 * 14/03/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Duitama, Boyaca.
 * All rights reserved.
 */
package com.sysman.util.rest;


/**
 * Modelo para el parametro que se consulta de la B hacia la C
 * 
 * @version 1.0,  14/03/2025}
 *  @author lvega
 *
 */

public class Parametro {
	
	private String compania;
	
	
	private String nombreParametro;
	
	
	private String aplicacion;

	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}


	/**
	 * @param compania the compania to set
	 */
	public void setCompania(String compania) {
		this.compania = compania;
	}


	/**
	 * @return the nombreParametro
	 */
	public String getNombreParametro() {
		return nombreParametro;
	}


	/**
	 * @param nombreParametro the nombreParametro to set
	 */
	public void setNombreParametro(String nombreParametro) {
		this.nombreParametro = nombreParametro;
	}


	/**
	 * @return the aplicacion
	 */
	public String getAplicacion() {
		return aplicacion;
	}


	/**
	 * @param aplicacion the aplicacion to set
	 */
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}

}



