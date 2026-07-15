/*
 * InformecompensacionfresumensControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InformecompensacionfresumensControladorUrlEnum {

    URL4438("INFORMECOMPENSACIONFRESUMENSCONTROLADORURL4438", "4001"),

    URL7704("INFORMECOMPENSACIONFRESUMENSCONTROLADORURL7704", "537007"),

    URL5196("INFORMECOMPENSACIONFRESUMENSCONTROLADORURL5196", "7030"),

    URL5197("INFORMECOMPENSACIONFRESUMENSCONTROLADORURL5197", "471029");

    private final String key;
    private final String value;

    private InformecompensacionfresumensControladorUrlEnum(String key,
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
