/*
 * PeriodoAlmacensControladorUrlEnum
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
public enum PeriodoAlmacensControladorUrlEnum {

    URL5118("PERIODOALMACENSCONTROLADORURL5118",
                    "7001"),

    URL4578("PERIODOALMACENSCONTROLADORURL4578",
                    "4001"),
    
    URL6969("PERIODOALMACENSCONTROLADORURL6969",
                    "139007"),
    
    URL7666("PERIODOALMACENSCONTROLADORURL7666",
                    "7021"),
    
    URL8999("PERIODOALMACENSCONTROLADORURL8999",
                    "148001"),

    URL5952("PERIODOALMACENSCONTROLADORURL5952",
                    "15042");

    private final String key;
    private final String value;

    private PeriodoAlmacensControladorUrlEnum(String key, String value) {
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
