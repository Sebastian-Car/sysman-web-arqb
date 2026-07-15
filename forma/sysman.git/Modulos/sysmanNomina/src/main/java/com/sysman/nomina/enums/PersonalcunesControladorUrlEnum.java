/*
 * PersonalsControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PersonalcunesControladorUrlEnum {

    URL151("CUNEPAGINADO", "210151"),

    URL634("TIPOVINCULACION", "634001"),

    URL1744("MEDIODEPAGO", "1744001"),

    URL459("BANCOSNOMINA", "459001"),

    URL210PUT("CUNEPUT", "210153"),

    ;

    private final String key;
    private final String value;

    private PersonalcunesControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
