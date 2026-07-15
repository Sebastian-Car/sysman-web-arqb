/*
 * RegistroejecucgastosrpsControladorUrlEnum
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
public enum RegistroejecucgastosrpsControladorUrlEnum {

    URL4977("REGISTROEJECUCGASTOSRPSCONTROLADORURL4977", "4007"),

    URL5948("REGISTROEJECUCGASTOSRPSCONTROLADORURL5948", "94088"),

    URL7089("REGISTROEJECUCGASTOSRPSCONTROLADORURL7089", "94090"),

    URL5397("REGISTROEJECUCGASTOSRPSCONTROLADORURL5397", "7019");

    private final String key;
    private final String value;

    private RegistroejecucgastosrpsControladorUrlEnum(String key,
        String value) {
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
