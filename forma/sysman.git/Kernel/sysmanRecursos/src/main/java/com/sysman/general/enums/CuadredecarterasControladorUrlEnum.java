/*
 * BalancePruebaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados
 * en el refactoring y asociados al codigo legacy obtenido con patrones de
 * busqueda.
 */
public enum CuadredecarterasControladorUrlEnum {

    URL001("CUADREDECARTERACONTROLADORURL001", "14036"), // Terceros

    URL002("CUADREDECARTERACONTROLADORURL002", "16209"), // cuentaInicial

    URL39100("CUADREDECARTERACONTROLADORURL39100", "39100"),

    URL39101("CUADREDECARTERACONTROLADORURL39101", "39101"),

    URL003("CUADREDECARTERACONTROLADORURL003", "39095"), // grilla

    URL004("CUADREDECARTERACONTROLADORURL004", "39097"), // update

    URL005("CUADREDECARTERACONTROLADORURL005", "16207"), // cuentasFinal

    URL39103("CUADREDECARTERACONTROLADORURL39103", "39103"),
    ;

    private final String key;
    private final String value;

    private CuadredecarterasControladorUrlEnum(String key, String value) {
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
