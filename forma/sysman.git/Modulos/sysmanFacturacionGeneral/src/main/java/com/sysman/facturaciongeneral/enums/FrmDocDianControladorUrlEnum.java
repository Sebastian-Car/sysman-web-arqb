/*
 * FrmFacEstadoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmDocDianControladorUrlEnum {

    URL9848("FRMDOCDIANCONTROLADORURL9848",
                    "185600C"),

    URL5474("FRMDOCDIANCONTROLADORURL5474",
                    "1856003"),

    URL3561("FRMDOCDIANCONTROLADORURL3651",
                    "1856004"),

    URL8245("FRMDOCDIANCONTROLADORURL8245",
                    "666012"),

    URL2735("FRMDOCDIANCONTROLADORURL2735",
                    "665025"),

    URL1987("FRMDOCDIANCONTROLADORURL1987",
                    "1857003"),

    URL7456("FRMDOCDIANCONTROLADORURL7456",
                    "185700C"),

    URL3562("FRMDOCDIANCONTROLADORURL7456",
                    "1857004"),

    URL665023("FRMDOCDIANCONTROLADORURL662023",
                    "665023"),
    
    URL665033("FRMDOCDIANCONTROLADORURL665033",
    		"665033"),

    ;

    private final String key;
    private final String value;

    private FrmDocDianControladorUrlEnum(String key, String value) {
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
