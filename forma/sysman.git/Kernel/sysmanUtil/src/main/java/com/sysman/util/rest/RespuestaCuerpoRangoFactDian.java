/*-
 * RespuestaCuerpoRangoFactDian.java
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

/**
 * Respuesta Cuerpo Rango Facturacion Dian
 * 
 * @version 1.0, 22/12/2020
 * @author eamaya
 *
 */
public class RespuestaCuerpoRangoFactDian {

    private String OperationCode;

    private String OperationDescription;

    private RespuestaResponseList ResponseList;

    public String getOperationCode() {
        return OperationCode;
    }

    public void setOperationCode(String operationCode) {
        OperationCode = operationCode;
    }

    public String getOperationDescription() {
        return OperationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        OperationDescription = operationDescription;
    }

    public RespuestaResponseList getResponseList() {
        return ResponseList;
    }

    public void setResponseList(RespuestaResponseList responseList) {
        ResponseList = responseList;
    }

}
