/*
 * FormatoDIANRetControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FormatoDIANRetControladorUrlEnum {

    URL3612("FORMATODIANRETCONTROLADORURL3612", "4001"),

    URL3916("FORMATODIANRETCONTROLADORURL3916", "7006"),

    URL12095("FORMATODIANRETCONTROLADORURL12095", "16014"),

    URL1624("FORMATODIANRETCONTROLADORURL1624", "16015"),
    
    URL5478("FORMATODIANRETCONTROLADORURL5478", "16173"),
    
    URL2456("FORMATODIANRETCONTROLADORURL2456", "16174"),
    
    URL16223("FORMATODIANRETCONTROLADORURL16223", "16223"),
    
    URL16224("FORMATODIANRETCONTROLADORURL16223", "16224");

    private final String key;
    private final String value;

    private FormatoDIANRetControladorUrlEnum(String key, String value) {
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
