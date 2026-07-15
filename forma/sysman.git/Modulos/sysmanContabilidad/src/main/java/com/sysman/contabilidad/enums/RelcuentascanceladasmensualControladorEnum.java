/*
 * RelcuentascanceladasmensualControladorEnum
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
public enum RelcuentascanceladasmensualControladorEnum {
    MES("mes"),
    ANIO("anio"),
    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),
    PR_DEPARTAMENTOCOMPANIA("PR_DEPARTAMENTOCOMPANIA"),
    PR_COMPANIA("PR_COMPANIA"),
    PR_TITULO1_CUENTAS_CANCELADAS("PR_TITULO1_CUENTAS_CANCELADAS"),
    TITULO1_CUENTAS_CANCELADAS("TITULO1 CUENTAS CANCELADAS"),
    NOMBREINFORME("000797RelCuentasCanceladas"),
    SYSDATE("SYSDATE"),
    IDIOMA1("TB_TB500");

    private final String value;

    private  RelcuentascanceladasmensualControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
