/*
 * FrmComisionesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos
 * Map<String,String> y disponibles en dicha enumeración.
 */
public enum FrmComisionesControladorEnum {

    NOMBRERESPONSABLE("NOMBRERESPONSABLE"),

    NOMBRETERCERO("NOMBRETERCERO"),

    NOMBREDEPENDENCIA("NOMBREDEPENDENCIA"),

    NOMBREBANCO("NOMBREBANCO"),

    CODTIPO("CODTIPO"),

    FECHAINICIO("FECHAINICIO"),

    FECHAFIN("FECHAFIN"),

    PAIS_DESTINO("PAIS_DESTINO"),

    PAIS_ORIGEN("PAIS_ORIGEN"),

    NOMBRECLASETRANSPORTE("NOMBRECLASETRANSPORTE"),

    NOMBRE_EXPE("NOMBRE_EXPE");

    private final String value;

    private FrmComisionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
