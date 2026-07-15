/*
 * FrmnovedadesplanoControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmnovedadesplanoControladorEnum {

    ANIO("ANO"),
    PER("PERIODO"),
    CIC("CICLO"),
    PARAM0("DATECREATED"),
    CONCEP("CONCEPTO"),
    CODEXTER("CODIGO_EXTERNO"),
    TABLA("SP_FACTURADO_EXTERNO"),
    CODRUTA("CODIGORUTA"),
    CODEXTERNO("CODEXTERNO");
    
    

    private final String value;

    private FrmnovedadesplanoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
