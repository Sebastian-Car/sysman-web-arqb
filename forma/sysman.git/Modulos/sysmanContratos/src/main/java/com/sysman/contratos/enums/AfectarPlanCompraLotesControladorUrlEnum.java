/*
 * AfectarPlanCompraLotesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AfectarPlanCompraLotesControladorUrlEnum {

    URL4068("AFECTARPLANCOMPRALOTESCONTROLADORURL4068",
                    "82039"),

    URL5073("AFECTARPLANCOMPRALOTESCONTROLADORURL5073",
                    "4001"),

    URL3196("AFECTARPLANCOMPRALOTESCONTROLADORURL3196",
                    "82038"),

    URL3169("AFECTARPLANCOMPRALOTESCONTROLADORURL3169",
                    "73006");

    private final String key;
    private final String value;

    private AfectarPlanCompraLotesControladorUrlEnum(String key, String value) {
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
