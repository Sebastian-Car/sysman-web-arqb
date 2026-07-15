/*
 * FrmresumenapropiacionesControladorUrlEnum
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
public enum FrmresumenapropiacionesControladorUrlEnum {

    URL3756("FRMRESUMENAPROPIACIONESCONTROLADORURL3756",
                    "7007"),

    URL4732("FRMRESUMENAPROPIACIONESCONTROLADORURL4732",
                    "94036"),

    URL4201("FRMRESUMENAPROPIACIONESCONTROLADORURL4201",
                    "4002"),

    URL5617("FRMRESUMENAPROPIACIONESCONTROLADORURL5617",
                    "94034");

    private final String key;
    private final String value;

    private FrmresumenapropiacionesControladorUrlEnum(String key,
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
