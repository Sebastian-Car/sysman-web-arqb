/*
 * ResumenPorDependenciasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ResumenPorDependenciasControladorEnum {

    ID_DE_PROCESO("ID_DE_PROCESO"),

    PR_DEPENDENCIA("PR_DEPENDENCIA"),

    REPORTE000194("000194ResumenPorDependenciasChivor");

    private final String value;

    private ResumenPorDependenciasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
