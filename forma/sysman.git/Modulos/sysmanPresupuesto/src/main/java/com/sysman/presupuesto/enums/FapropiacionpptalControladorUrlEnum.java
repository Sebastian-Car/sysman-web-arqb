/*
 * FapropiacionpptalControladorUrlEnum
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
public enum FapropiacionpptalControladorUrlEnum {

    URL5264("FAPROPIACIONPPTALCONTROLADORURL5264", "4007"),

    URL5780("FAPROPIACIONPPTALCONTROLADORURL5780", "94008"),

    URL4768("FAPROPIACIONPPTALCONTROLADORURL4768", "7007"),

    URL6546("FAPROPIACIONPPTALCONTROLADORURL6546", "94010");

    private final String key;
    private final String value;

    private FapropiacionpptalControladorUrlEnum(String key, String value) {
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
