/*
 * VisadoChequesControladorEnum
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
public enum VisadoChequesControladorEnum {


    ANIO("ANIO"),
    CUENTABANCARIAS("cuentaBancaria"),
    CUENTABANCARIA("CUENTABANCARIA"),
    CHEQUE("cheque"),
    PR_NRODOCUMENTO("PR_NRODOCUMENTO"),
    NOMBREINFORME("000755VisadodeCheques"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA");

    private final String value;

    private  VisadoChequesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
