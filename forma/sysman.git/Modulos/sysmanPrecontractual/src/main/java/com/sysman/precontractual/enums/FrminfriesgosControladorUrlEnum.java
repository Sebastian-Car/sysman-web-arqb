/*
 * FrminfriesgosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrminfriesgosControladorUrlEnum {

    URL2532("FRMINFRIESGOSCONTROLADORURL2532", "481005"),

    URL3369("FRMINFRIESGOSCONTROLADORURL3369", "481007");

    private final String key;
    private final String value;

    private FrminfriesgosControladorUrlEnum(String key, String value) {
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
