/*
 * EjecuciongastosguaisControladorUrlEnum
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
public enum EjecuciongastosguaisControladorUrlEnum {

    URL8497("EJECUCIONGASTOSGUAISCONTROLADORURL8497", "20032"),

    URL3953("EJECUCIONGASTOSGUAISCONTROLADORURL3953", "7007"),

    URL7154("EJECUCIONGASTOSGUAISCONTROLADORURL7154", "94034"),

    URL4671("EJECUCIONGASTOSGUAISCONTROLADORURL4671", "7007"),

    URL5381("EJECUCIONGASTOSGUAISCONTROLADORURL5381", "4002"),

    URL5975("EJECUCIONGASTOSGUAISCONTROLADORURL5975", "94036");

    private final String key;
    private final String value;

    private EjecuciongastosguaisControladorUrlEnum(String key, String value) {
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
