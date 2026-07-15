/*
 * SubproyectorubrosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubproyectorubrosControladorEnum {

    RANGOA("RANGOA"),

    RANGOB("RANGOB"),
    
    AUXILIAR("AUXILIAR"),

    DIMENSION("DIMENSION"),

    PROGRAMA("PROGRAMA"),

    SUBPROGRAMA("SUBPROGRAMA"),

    FUENTERECURSOSRUBRO("FUENTERECURSOSRUBRO"),

    RUBROPPTALES("RUBROPPTALES"),

    ID("ID"),

    NOMBRE_RUBRO_CDP("NOMBRE_RUBRO_CDP"),
    
    NOMBRE_AUXILIAR_GENERAL("NOMBRE_AUXILIAR_GENERAL"),

    NOMBRE_DIMENSION("NOMBRE_DIMENSION"),

    NOMBRE_SECTOR("NOMBRE_SECTOR"),

    NOMBRE_PROGRAMA("NOMBRE_PROGRAMA"),

    NOMBRE_SUB_PROGRAMA("NOMBRE_SUB_PROGRAMA"),

    NOMBRE_FUENTE_RECURSOS("NOMBRE_FUENTE_RECURSOS"),

    NOMBRE_RUBRO("NOMBRE_RUBRO"),

    NOMBRE_REFERENCIA("NOMBRE_REFERENCIA"),

    NOMBRE_CENTRO_COSTO("NOMBRE_CENTRO_COSTO"),

    CENTRO_COSTO("CENTRO_COSTO"),

    REFERENCIA("REFERENCIA");

    private final String value;

    private SubproyectorubrosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
