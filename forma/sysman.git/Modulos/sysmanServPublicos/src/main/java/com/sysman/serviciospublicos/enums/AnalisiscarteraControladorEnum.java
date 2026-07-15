/*
 * AnalisiscarteraControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum AnalisiscarteraControladorEnum {
    INFORME1241("001241AnalisisCartera"),
    INFORME1243("001243AnalisisCarteraCOS"),
    MSM_INFORME_NO_EXISTE("MSM_INFORME_NO_EXISTE"),
    FORMATO_CALIDAD("FORMATO CALIDAD"),
    CODIGO_INICIAL("CODIGO_INICIAL"),
    SI("SI"),
    NO("NO"),
    CICLOFINAL("cicloFinal"),
    CONDICION("CONDICION"),
    CICLOINICIAL("cicloInicial"),
    PR_COMPANIA("PR_COMPANIA"),
    PR_FORMS_ANALISISCARTERA_CICLO("PR_FORMS_ANALISISCARTERA_CICLO"),
    PR_FORMS_ANALISISCARTERA_CMBCICLOF("PR_FORMS_ANALISISCARTERA_CMBCICLOF"),
    TB_TB1801("TB_TB1801"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA");

    private final String value;

    private  AnalisiscarteraControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
