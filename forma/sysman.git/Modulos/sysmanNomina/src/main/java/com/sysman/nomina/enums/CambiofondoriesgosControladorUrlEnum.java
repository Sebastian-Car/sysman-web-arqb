/*
 * CambiofondoriesgosControladorUrlEnum
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
public enum CambiofondoriesgosControladorUrlEnum {

    URL2205("CAMBIOFONDORIESGOSCONTROLADORURL2205",
                    "461001"),

    URL2951("CAMBIOFONDORIESGOSCONTROLADORURL2951",
                    "210003");

    private final String key;
    private final String value;

    private CambiofondoriesgosControladorUrlEnum(String key, String value) {
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
