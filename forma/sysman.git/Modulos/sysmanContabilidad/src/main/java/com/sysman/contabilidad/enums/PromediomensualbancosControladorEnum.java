/*
 * PromediomensualbancosControladorEnum
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
public enum PromediomensualbancosControladorEnum {
    ANO("ano"),
    ULTIMODIA("ultimoDia"),
    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),
    TG_NOMBRE_DE_LA_ENTIDAD("TG_NOMBRE_DE_LA_ENTIDAD"),
    IDIOMA1("TB_TB439"),
    IDIOMA2("TB_TB438"),
    PR_NITCOMPANIA("PR_NITCOMPANIA"),
    PR_MESINFORMADO("PR_MESINFORMADO"),
    PR_ENMILES("PR_ENMILES"),
    NOMBREINFORME("000798PromedioMensualBancos"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),
    MILES("miles"),
    MES("mes");

    private final String value;

    private  PromediomensualbancosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
