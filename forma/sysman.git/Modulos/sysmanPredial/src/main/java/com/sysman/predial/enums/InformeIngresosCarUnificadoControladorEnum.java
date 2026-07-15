/*
 * InformeIngresosCarUnificadoControladorEnum
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
public enum InformeIngresosCarUnificadoControladorEnum {
    PR_FECHAF("PR_FORMS_RELACION_INGRESOS_CAR_159_FECHAF"), PR_FECHAI("PR_FORMS_RELACION_INGRESOS_CAR_159_FECHAI"), PR_NOMBRECOMPANIA(
                    "PR_NOMBRECOMPANIA"), PR_NITCOMPANIA("PR_NITCOMPANIA"), FORMATOFECHA("dd/MM/yyyy"), REPORTE001411(
                                    "001411PREDIALINFCAR"), CODIGOBANCO("CODIGOBANCO"), BANCOINICIAL("BANCOINICIAL");

    private final String value;

    private InformeIngresosCarUnificadoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
