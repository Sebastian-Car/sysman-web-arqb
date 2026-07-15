/*
 * FrmincrementosautoavaluosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmincrementosautoavaluosControladorUrlEnum {

    URL18760("FRMINCREMENTOSAUTOAVALUOSCONTROLADORURL18760",
                    "405001"), URL18761(
                                    "FRMINCREMENTOSAUTOAVALUOSCONTROLADORURL18760",
                                    "405003");

    private final String key;
    private final String value;

    private FrmincrementosautoavaluosControladorUrlEnum(String key,
        String value) {
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
