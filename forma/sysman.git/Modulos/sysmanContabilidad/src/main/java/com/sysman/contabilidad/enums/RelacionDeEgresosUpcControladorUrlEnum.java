/*
 * RelacionDeEgresosUpcControladorUrlEnum
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
 * con patrones de la busqueda.
 */
public enum RelacionDeEgresosUpcControladorUrlEnum {

    URL16016("RELACIONDEEGRESOSUPCCONTROLADORURL16016", "16016"),

    URL16018("RELACIONDEEGRESOSUPCCONTROLADORURL16018", "16018"),
    
    URL6234("RELACIONDEEGRESOSUPCCONTROLADORURL6234", "13003"),

    URL6235("RELACIONDEEGRESOSUPCCONTROLADORURL6234", "13005");

    private final String key;
    private final String value;

    private RelacionDeEgresosUpcControladorUrlEnum(String key, String value) {
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
