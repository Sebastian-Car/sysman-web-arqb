/*
 * ImpuestoCreeControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ImpuestoCreeControladorUrlEnum {
    URL4922("IMPUESTOCREECONTROLADORURL4922","214030"),
    URL4948("IMPUESTOCREECONTROLADORURL4948","214031");
    private final String key;
    private final String value;

    private  ImpuestoCreeControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
