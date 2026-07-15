/*
 * NatSubArpsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum NatSubArpsControladorUrlEnum {

    URL6154("NATSUBARPSCONTROLADORURL6154","642002"),
    URL7851("NATSUBARPSCONTROLADORURL7851","643002"),
    URL5412("NATSUBARPSCONTROLADORURL5412","638008");

    private final String key;
    private final String value;

    private  NatSubArpsControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
