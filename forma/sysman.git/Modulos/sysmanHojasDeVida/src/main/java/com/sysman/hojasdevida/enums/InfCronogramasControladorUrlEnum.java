/*
 * InfCronogramasControladorUrlEnum
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
public enum InfCronogramasControladorUrlEnum {
    // PARAMS: COMPANIA->COMPANIA, ACTIVIDADI - ACTIVIDADF
    URL4381("INFCRONOGRAMASCONTROLADORURL4381", "724013"),
    // PARAMS: COMPANIA->COMPANIA, CODIGO_ACTIVIDAD BETWEEN ACTIVIDADI
    // AND ACTIVIDADF
    URL4522("INFCRONOGRAMASCONTROLADORURL4522", "724011"),
    // PARAMS:
    URL4994("INFCRONOGRAMASCONTROLADORURL4994", "726009"),

    URL5012("INFCRONOGRAMASCONTROLADORURL5012", "726011"),

    URL4150("INFCRONOGRAMASCONTROLADORURL4150", "727001");

    private final String key;
    private final String value;

    private InfCronogramasControladorUrlEnum(String key, String value) {
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
