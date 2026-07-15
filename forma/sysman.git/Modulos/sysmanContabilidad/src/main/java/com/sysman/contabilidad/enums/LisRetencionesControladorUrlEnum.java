/*
 * LisRetencionesControladorUrlEnum
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
public enum LisRetencionesControladorUrlEnum {

    URL3495("LISRETENCIONESCONTROLADORURL3495", "8001"),
    
    URL3496("LISRETENCIONESCONTROLADORURL3496", "8007"),

    URL3236("LISRETENCIONESCONTROLADORURL3236", "4001");

    private final String key;
    private final String value;

    private LisRetencionesControladorUrlEnum(String key, String value) {
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
