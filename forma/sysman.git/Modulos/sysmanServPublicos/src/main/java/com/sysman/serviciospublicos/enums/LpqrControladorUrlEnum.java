/*
 * LpqrControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LpqrControladorUrlEnum {

    URL12767("LPQRCONTROLADORURL12767", "47005"),

    URL10672("LPQRCONTROLADORURL10672", "366014"),

    URL12155("LPQRCONTROLADORURL12155", "52001"),

    URL14283("LPQRCONTROLADORURL14283", "234006"),

    URL15647("LPQRCONTROLADORURL15647", "234008"),

    URL9938("LPQRCONTROLADORURL9938", "214029"),

    URL11792("LPQRCONTROLADORURL11792", "366016"),

    URL11793("LPQRCONTROLADORURL11793", "341001"),

    URL11794("LPQRCONTROLADORURL11794", "341002");

    private final String key;
    private final String value;

    private LpqrControladorUrlEnum(String key, String value) {
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
