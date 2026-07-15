/*-
 * RespuestaCuerpoFormatoConsultas.java
 *
 * 1.0
 * 
 * 22/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * Respuesta del cuerpo de formato de consultas
 * 
 * @version 1.0, 22/12/2020
 * @author eamaya
 * 
 * @version 1.1, 08/10/2021
 * @author gfigueredo
 * Se adiciona el atributo XmlDocumentKey, que representa el codigo
 * CUFE (para facturas) y CUDE (para notas)
 *
 */
public class RespuestaCuerpoFormatoConsultas {

    private String StatusDescription;

    private String XmlDocumentKey;
    
    private List<String> ErrorMessage;

    private boolean IsValid;

    public String getStatusDescription() {
        return StatusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        StatusDescription = statusDescription;
    }

    public List<String> getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        ErrorMessage = errorMessage;
    }

    public boolean isIsValid() {
        return IsValid;
    }

    public void setIsValid(boolean isValid) {
        IsValid = isValid;
    }

	public String getXmlDocumentKey() {
		return XmlDocumentKey;
	}

	public void setXmlDocumentKey(String xmlDocumentKey) {
		XmlDocumentKey = xmlDocumentKey;
	}

}
