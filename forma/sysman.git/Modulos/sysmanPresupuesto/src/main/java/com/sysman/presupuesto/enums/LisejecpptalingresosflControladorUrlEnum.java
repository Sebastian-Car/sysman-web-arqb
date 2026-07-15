/*
 * LisejecpptalingresosflControladorUrlEnum
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
public enum LisejecpptalingresosflControladorUrlEnum {

    URL7664("LISEJECPPTALINGRESOSFLCONTROLADORURL",
                    "4001"),

    URL9268("LISEJECPPTALINGRESOSFLCONTROLADORURL9268",
                    "7001"),

    URL10828("LISEJECPPTALINGRESOSFLCONTROLADORURL10828",
                    "94074"),

    URL11906("LISEJECPPTALINGRESOSFLCONTROLADORURL11906",
                    "23010"),

    URL12612("LISEJECPPTALINGRESOSFLCONTROLADORURL12612",
                    "23019"),

    URL9889("LISEJECPPTALINGRESOSFLCONTROLADORURL9889",
                    "94072");

    private final String key;
    private final String value;

    private LisejecpptalingresosflControladorUrlEnum(String key, String value) {
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
