/*
 * FrmprediosporprescribirControladorEnum
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
public enum FrmprediosporprescribirControladorEnum {
    CODIGO_INICIAL("CODIGO_INICIAL"), NUMERO_ORDEN_PREDIAL("NUMERO_ORDEN_PREDIAL"), PR_VISIBLE("PR_VISIBLE"), PR_NOMBRECOMPANIA(
                    "PR_NOMBRECOMPANIA"), PR_NITCOMPANIA(
                                    "PR_NITCOMPANIA"), REPORTE000855(
                                                    "000855INFPREDIOSPORPRESCRIPCION");

    private final String value;

    private FrmprediosporprescribirControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
