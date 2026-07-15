/*
 * FEjecDeIngresosControladorUrlEnum
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
public enum FEjecDeIngresosControladorUrlEnum {

    URL4692("FEJECDEINGRESOSCONTROLADORURL4692",
                    "45002"),

    URL5334("FEJECDEINGRESOSCONTROLADORURL5334",
                    "45059"),

    URL4229("FEJECDEINGRESOSCONTROLADORURL4229",
                    "4001");

    private final String key;
    private final String value;

    private FEjecDeIngresosControladorUrlEnum(String key, String value) {
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
