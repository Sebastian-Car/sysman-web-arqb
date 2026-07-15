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

package com.sysman.util.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase estandar para manejar la informacion del cuerpo que genera la respuesta de todos los servicios con
 * el fin de enviar los errores de negocio claramente
 * 
 * @version 1.1, 15/05/2023
 * @author gportilla
 *
 */
@XmlRootElement
public class RespuestaCuerpo {
    /**
     * Descripcion de la respuesta
     */
    private String descripcion;
    /**
     * Indica si la respuesta es valida
     */
    private Boolean valido;
    /**
     * Lista de errores generados
     */
    private List<String> acumuladoErrores;

    /**
     * Constructor, el cual por defecto deja la descripcion = null, valido = false y acumuladoErrores =
     * como una lista vacia
     */
    public RespuestaCuerpo() {
    	descripcion = null;
    	valido = false;
    	acumuladoErrores = new ArrayList<String>();
    }

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getValido() {
		return valido;
	}

	public void setValido(Boolean valido) {
		this.valido = valido;
	}

	public List<String> getAcumuladoErrores() {
		return acumuladoErrores;
	}

	public void setAcumuladoErrores(List<String> acumuladoErrores) {
		this.acumuladoErrores = acumuladoErrores;
	}

   

}
