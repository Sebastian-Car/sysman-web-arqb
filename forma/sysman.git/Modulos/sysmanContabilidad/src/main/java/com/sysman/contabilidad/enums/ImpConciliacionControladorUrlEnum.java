/*
 * ImpConciliacionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImpConciliacionControladorUrlEnum {

    URL27777("IMPCONCILIACIONCONTROLADORURL27777",
                    "15016"),

    URL69696("IMPCONCILIACIONCONTROLADORURL69696",
                    "16155"),

    URL13666("IMPCONCILIACIONCONTROLADORURL13666",
                    "16158"),

    URL26660("IMPCONCILIACIONCONTROLADORURL26660",
                    "16156"),

    URL25244("IMPCONCILIACIONCONTROLADORURL25244",
                    "16157"),
	
	URL1942001("IMPCONCILIACIONCONTROLADORURL1942001",
            "1942001"),
	
	URL1942002("IMPCONCILIACIONCONTROLADORURL1942002",
            "1942002"),
	
	URL1942003("IMPCONCILIACIONCONTROLADORURL1942003",
            "1942003");

    private final String key;
    private final String value;

    private ImpConciliacionControladorUrlEnum(String key, String value) {
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
