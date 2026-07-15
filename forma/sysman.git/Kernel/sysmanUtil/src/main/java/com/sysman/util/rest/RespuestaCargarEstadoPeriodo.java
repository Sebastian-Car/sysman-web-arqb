/*-
 * RespuestaApi.java
 *
 * 1.0
 * 
 * 26/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase estandar para generar la respuesta de todos los servicios con
 * el fin de enviar los errores de negocio claramente
 * 
 * @version 1.1, 27/02/2021
 * @author jacevedo
 *
 */
@XmlRootElement
public class RespuestaCargarEstadoPeriodo {
	private String estado;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}


}
