/*-
 * RespuestaApiSigec.java
 *
 * 1.0
 * 
 * 02/10/2024
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
 * @version 1.1, 02/10/2024
 * @author mrosero
 *
 */
@XmlRootElement
public class RespuestaApiSigec {
    /**
     */
    int httpstatus;
    /**
     */
    Object data;
    /**
     */
    String message;
    
    public RespuestaApiSigec() {
    	data = null;
        httpstatus = 200;
        message = "OK";
    }
    
	public int getHttpstatus() {
		return httpstatus;
	}
	public void setHttpstatus(int httpstatus) {
		this.httpstatus = httpstatus;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
