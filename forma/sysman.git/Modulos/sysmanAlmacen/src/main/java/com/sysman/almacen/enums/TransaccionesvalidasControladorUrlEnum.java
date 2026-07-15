/*
 * TransaccionesvalidasControladorUrlEnum
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
public enum TransaccionesvalidasControladorUrlEnum {

    URL4447("TRANSACCIONESVALIDASCONTROLADORURL4447", "226001"),

    URL4882("TRANSACCIONESVALIDASCONTROLADORURL4882", "102008"),

    URL0001("TRANSACCIONESVALIDASCONTROLADORURL0001", "148003");

    private final String key;
    private final String value;

    private TransaccionesvalidasControladorUrlEnum(String key, String value) {
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
