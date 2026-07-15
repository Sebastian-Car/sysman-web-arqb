/*
 * ReferenciaControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ReferenciaControladorUrlEnum {

    URL2544("REFERENCIACONTROLADORURL2544",
                    "4001"),

    URL2975("REFERENCIACONTROLADORURL2975",
                    "4001"),
    
    URL2976("REFERENCIACONTROLADORURL2976",
            "32059"),
	 URL13050("REFERENCIACONTROLADORURL2976",
	            "13050");

    private final String key;
    private final String value;

    private ReferenciaControladorUrlEnum(String key, String value) {
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
