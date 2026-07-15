/*-
 * RespuestaFridaLegalizarCuerpo.java
 *
 * 1.0
 * 
 * 8/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * cuerpo de la respuesta al servicio de legalizar factura
 * 
 * @version 1.0, 8/01/2021
 * @author eamaya
 *
 */
public class RespuestaFridaLegalizarCuerpoDian {

    private String XmlBytes;
    private String StatusDescription;
    private String StatusMessage;
    private String XmlDocumentKey;
    private String XmlBase64Bytes;
    private Object ErrorMessage;
    private String StatusCode;
    private boolean IsValid;
    private String XmlFileName;

    public String getXmlBytes() {
        return XmlBytes;
    }

    public void setXmlBytes(String xmlBytes) {
        XmlBytes = xmlBytes;
    }

    public String getStatusDescription() {
        return StatusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        StatusDescription = statusDescription;
    }

    public String getXmlDocumentKey() {
        return XmlDocumentKey;
    }

    public void setXmlDocumentKey(String xmlDocumentKey) {
        XmlDocumentKey = xmlDocumentKey;
    }

    public String getXmlBase64Bytes() {
        return XmlBase64Bytes;
    }

    public void setXmlBase64Bytes(String xmlBase64Bytes) {
        XmlBase64Bytes = xmlBase64Bytes;
    }

    public Object getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(Object errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(String statusCode) {
        StatusCode = statusCode;
    }

    public boolean isIsValid() {
        return IsValid;
    }

    public void setIsValid(boolean isValid) {
        IsValid = isValid;
    }

	public String getStatusMessage() {
		return StatusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		StatusMessage = statusMessage;
	}

	public String getXmlFileName() {
		return XmlFileName;
	}

	public void setXmlFileName(String xmlFileName) {
		XmlFileName = xmlFileName;
	}

}
