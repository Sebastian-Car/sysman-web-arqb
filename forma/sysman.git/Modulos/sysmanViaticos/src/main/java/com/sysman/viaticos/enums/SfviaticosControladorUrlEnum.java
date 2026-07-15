/*
 * SfviaticosControladorUrlEnum
 *
 * 1.0
 *
 * 19/01/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SfviaticosControladorUrlEnum {

    URL4130("SFVIATICOSCONTROLADORURL4130", "45046"),

    URL4131("SFVIATICOSCONTROLADORURL4131", "766007"),

    URL4132("SFVIATICOSCONTROLADORURL4132", "210113"),

    URL4133("SFVIATICOSCONTROLADORURL4133", "764012"),

    URL5151("SFVIATICOSCONTROLADORURL4133", "210103")

    ;

    private final String key;
    private final String value;

    private SfviaticosControladorUrlEnum(String key, String value) {
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
