/*
 * BoletinDiarioCajaControladorUrlEnum
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
public enum BoletinDiarioCajaControladorUrlEnum {

    URL4067("BOLETINDIARIOCAJACONTROLADORURL4067", "29025"),

    URL4068("BOLETINDIARIOCAJACONTROLADORURL4068", "39012");

    private final String key;
    private final String value;

    private BoletinDiarioCajaControladorUrlEnum(String key, String value) {
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
