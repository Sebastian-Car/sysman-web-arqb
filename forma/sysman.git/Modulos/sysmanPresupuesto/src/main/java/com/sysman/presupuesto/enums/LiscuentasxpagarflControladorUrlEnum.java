/*
 * LiscuentasxpagarflControladorUrlEnum
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
public enum LiscuentasxpagarflControladorUrlEnum {

    URL7251("LISCUENTASXPAGARFLCONTROLADORURL7251", "94070"),

    URL4499("LISCUENTASXPAGARFLCONTROLADORURL4499", "4001"),

    URL4020("LISCUENTASXPAGARFLCONTROLADORURL4020", "25008"),

    URL5046("LISCUENTASXPAGARFLCONTROLADORURL5046", "14059"),

    URL6616("LISCUENTASXPAGARFLCONTROLADORURL6616", "94068"),

    URL5770("LISCUENTASXPAGARFLCONTROLADORURL5770", "14065");

    private final String key;
    private final String value;

    private LiscuentasxpagarflControladorUrlEnum(String key, String value) {
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
