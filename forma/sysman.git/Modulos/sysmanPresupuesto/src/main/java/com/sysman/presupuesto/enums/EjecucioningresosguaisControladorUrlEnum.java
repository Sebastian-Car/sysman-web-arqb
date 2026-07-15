/*
 * EjecucioningresosguaisControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EjecucioningresosguaisControladorUrlEnum {

    URL5636("EJECUCIONINGRESOSGUAISCONTROLADORURL5636", "94022"),

    URL4056("EJECUCIONINGRESOSGUAISCONTROLADORURL4056", "7007"),

    URL6515("EJECUCIONINGRESOSGUAISCONTROLADORURL6515", "94024"),

    URL7723("EJECUCIONINGRESOSGUAISCONTROLADORURL7723", "20013"),

    URL4601("EJECUCIONINGRESOSGUAISCONTROLADORURL4601", "7012"),

    URL5085("EJECUCIONINGRESOSGUAISCONTROLADORURL5085", "4007");

    private final String key;
    private final String value;

    private EjecucioningresosguaisControladorUrlEnum(String key, String value) {
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
