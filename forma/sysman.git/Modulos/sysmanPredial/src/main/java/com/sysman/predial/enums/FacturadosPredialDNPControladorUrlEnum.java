/*
 * FacturadosPredialDNPControladorUrlEnum
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
public enum FacturadosPredialDNPControladorUrlEnum {

    URL9156("FACTURADOSPREDIALDNPCONTROLADORURL9156",
                    "381002"),

    URL8786("FACTURADOSPREDIALDNPCONTROLADORURL8786",
                    "381001");

    private final String key;
    private final String value;

    private FacturadosPredialDNPControladorUrlEnum(String key, String value) {
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
