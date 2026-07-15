/*
 * NovedadesControladorEnum
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
public enum InterrupcionvacacionesControladorEnum {
    TI_MS_ERROR_VALIDACION("TI_MS_ERROR_VALIDACION"),

    COMPANIA_AUX("COMPANIA_AUX"),

    ID_DE_PROCESO_AUX("ID_DE_PROCESO_AUX"),

    ID_DE_EMPLEADO_AUX("ID_DE_EMPLEADO_AUX"),

    PERIODO_AUX("PERIODO_AUX"),

    MES_AUX("MES_AUX"),

    ANO_AUX("ANO_AUX"),

    ID_DE_PROCESO("ID_DE_PROCESO"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    ID_EMPLEADO("ID_EMPLEADO"),

    PROCESO("PROCESO");

    private final String value;

    private InterrupcionvacacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
