/*
 * LicenciasControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum LicenciasControladorEnum {

    FECHA_INICIO("FECHA_INICIO"),

    FECHA_FINAL("FECHA_FINAL"),

    LICENCIA("LICENCIA"),

    PERIODO("PERIODO"),

    HABILES("HABILES"),

    DIAS("DIAS"),

    NUMEROPERIODO("NUMEROPERIODO"),

    DIAS_INICIALES("DIAS_INICIALES"),

    OBSERVACION_L("OBSERVACION_L"),

    FECHA_ACTO("FECHA_ACTO"),

    ADICIONAR("ADICIONAR"),

    BORRAR("BORRAR"),
    
    TOTAL_DIAS_COMISION("TOTAL_DIAS_COMISION"),
    
    FECHA_INICIO_COMISION("FECHA_INICIO_COMISION"),
    
    FECHA_FINAL_COMISION("FECHA_FINAL_COMISION"),
    
    ESTADO_ACTUAL("ESTADO_ACTUAL"),

    DIFERIR_FECHAS_CONTANDO_MESES_DE_31_Y_28_DIAS(
                    "DIFERIR FECHAS CONTANDO MESES DE 31 Y 28 DIAS");

    private final String value;

    private LicenciasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
