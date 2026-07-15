/*
 * MunicipioControladorUrlEnum
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
public enum MunicipioControladorUrlEnum {

    URL5893("MUNICIPIOCONTROLADORURL5893", "2007"),

    URL7250("MUNICIPIOCONTROLADORURL7250", ""),

    URL5331("MUNICIPIOCONTROLADORURL5331", "1003"),

    URL7807("MUNICIPIOCONTROLADORURL7807", ""),

    URL8406("MUNICIPIOCONTROLADORURL8406", ""),

    URL6498("MUNICIPIOCONTROLADORURL6498", "5007");

    private final String key;
    private final String value;

    private MunicipioControladorUrlEnum(String key, String value) {
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
