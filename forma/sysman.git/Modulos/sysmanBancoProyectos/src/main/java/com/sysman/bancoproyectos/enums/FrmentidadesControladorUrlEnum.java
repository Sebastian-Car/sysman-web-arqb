/*
 * FrmentidadesControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmentidadesControladorUrlEnum {

    URL0001("FRMENTIDADESCONTROLADORURL0001", "118024"),

    URL0002("FRMENTIDADESCONTROLADORURL0002", "118025"),

    URL2139("FRMENTIDADESCONTROLADORURL2139", "568001");

    private final String key;
    private final String value;

    private FrmentidadesControladorUrlEnum(String key, String value) {
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
