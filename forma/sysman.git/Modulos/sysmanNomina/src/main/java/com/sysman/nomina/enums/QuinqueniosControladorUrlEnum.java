/*
 * QuinqueniosControladorUrlEnum
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
public enum QuinqueniosControladorUrlEnum {

    URL0001("QUINQUENIOSCONTROLADORURL0001", "210047"),

    URL0002("QUINQUENIOSCONTROLADORURL0002", "210049"),

    URL5693("QUINQUENIOSCONTROLADORURL5693", "471043"),

    URL6490("QUINQUENIOSCONTROLADORURL6490", "471043"),

    URL11108("QUINQUENIOSCONTROLADORURL11108",
                    "Acciones.actualizar(con, \"PERSONAL\",");

    private final String key;
    private final String value;

    private QuinqueniosControladorUrlEnum(String key, String value) {
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
