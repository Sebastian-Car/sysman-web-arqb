/*
 * FrmeditarespecplacasControladorUrlEnum
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
public enum FrmeditarespecplacasControladorUrlEnum {

    URL0001("FRMEDITARESPECPLACASCONTROLADORURL0001", "141025"),

    URL0002("FRMEDITARESPECPLACASCONTROLADORURL0002", "141027"),

    URL3545("FRMEDITARESPECPLACASCONTROLADORURL3545", "145001");

    private final String key;
    private final String value;

    private FrmeditarespecplacasControladorUrlEnum(String key, String value) {
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
