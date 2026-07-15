/*
 * FrmMorososPersonaControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmMorososPersonaControladorEnum {
    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"), PR_MOSTRARDETALLE("PR_MOSTRARDETALLE"), REPORTE000913("000913INFDEUDAPROPIETARIO"), CODIGO(
                    "CODIGO"), NIT_INICIAL(
                                    "NIT_INICIAL"), CODIGOINICIAL("CODIGOINICIAL"), NIT("NIT");

    private final String value;

    private FrmMorososPersonaControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
