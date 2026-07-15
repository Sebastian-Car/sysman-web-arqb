/*
 * InvGralConsumoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InvGralConsumoControladorUrlEnum {

    URL3890("INVGRALCONSUMOCONTROLADORURL3890",
                    "112046"),

    URL4679("INVGRALCONSUMOCONTROLADORURL4679",
                    "62013"),

    URL5974("INVGRALCONSUMOCONTROLADORURL5974",
                    "112044"),

    URL5363("INVGRALCONSUMOCONTROLADORURL5363",
                    "62015");

    private final String key;
    private final String value;

    private InvGralConsumoControladorUrlEnum(String key, String value) {
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
