/*
 * EstablecimientosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EstablecimientosControladorUrlEnum {

    URL8770("ESTABLECIMIENTOSCONTROLADORURL8770",
                    "1001"),

    URL8311("ESTABLECIMIENTOSCONTROLADORURL8311",
                    "14001"),

    URL3656("ESTABLECIMIENTOSCONTROLADORURL3656",
                    "2001"),

    URL4587("ESTABLECIMIENTOSCONTROLADORURL4587",
                    "5001"),
    
    URL198("ESTABLECIMIENTOSCONTROLADORURL198",
                    "1774001");

    private final String key;
    private final String value;

    private EstablecimientosControladorUrlEnum(String key, String value) {
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
