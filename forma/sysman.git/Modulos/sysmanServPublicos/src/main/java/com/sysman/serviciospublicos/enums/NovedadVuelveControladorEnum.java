/*
 * NovedadVuelveControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum NovedadVuelveControladorEnum {

    PARAM0("GENERAR INFORME NOVEDADES EXTERNAS ENTRE FECHAS"),

    PARAM1("NOVEDADES EXTERNAS"),

    PARAM2("800118NovedadesAseo");

    private final String value;

    private NovedadVuelveControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
