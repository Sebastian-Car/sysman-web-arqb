/*
 * FrmInfEtapasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmInfEtapasControladorEnum {

    TIPOESTUDIO("TIPOESTUDIO"),

    REPORTE000548("000548ESINFETAPAS"),

    NOMBRE_ESTUDIO("NOMBRE_ESTUDIO"),

    ESTUDIO_INI("ESTUDIO_INI"),

    COD_ESTUDIO("COD_ESTUDIO"),

    TIPO_DIA("TIPO_DIA");

    private final String value;

    private FrmInfEtapasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
