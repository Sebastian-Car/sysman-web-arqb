/*
 * AcumuladosUnoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum GeneraProcesoSiaControladorUrlEnum {

    URL3933("GENERALPROCESOIACONTROLADORURL3933", "4001"),

    URL5973("GENERALPROCESOIACONTROLADORURL5973", "7001"),

    URL4509("GENERALPROCESOIACONTROLADORURL4509", "7012"),

    URL5057("GENERALPROCESOIACONTROLADORURL5057", "118031"),

    URL8762("GENERALPROCESOIACONTROLADORURL8762", "59027"),

    URL175("GENERALPROCESOIACONTROLADORURL175", "1750001"),
    
    URL175003("GENERALPROCESOIACONTROLADORURL175", "1750003"),
    
    URL1883009("GENERALPROCESOIACONTROLADORURL1883008","1883009");

    private final String key;
    private final String value;

    private GeneraProcesoSiaControladorUrlEnum(String key, String value) {
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
