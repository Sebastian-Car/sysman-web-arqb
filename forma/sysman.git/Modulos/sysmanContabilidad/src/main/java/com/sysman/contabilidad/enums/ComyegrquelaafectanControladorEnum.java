/*
 * ComyegrquelaafectanControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum ComyegrquelaafectanControladorEnum {

    TIPO("TIPO"), 
    TIPOS("TIPOS"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"), 
    ANIO("ANIO"),
    PTIPO("tipo"),
    NOMBREINFORME("000665COMyEGRQueLaAfectan"),  
    PR_ENCABEZADO("PR_ENCABEZADO"),
    COMPROBANTE("comprobante");

    private final String value;

    private  ComyegrquelaafectanControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
