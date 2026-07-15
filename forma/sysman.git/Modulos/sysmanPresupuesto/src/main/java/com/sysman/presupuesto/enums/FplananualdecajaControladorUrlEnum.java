/*
 * FplananualdecajaControladorUrlEnum
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
public enum FplananualdecajaControladorUrlEnum {

    URL3596("FPLANANUALDECAJACONTROLADORURL3596", "4002"),

    URL4086("FPLANANUALDECAJACONTROLADORURL4086", "94038"),

    URL4943("FPLANANUALDECAJACONTROLADORURL4943", "94048");

    private final String key;
    private final String value;

    private FplananualdecajaControladorUrlEnum(String key, String value) {
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
