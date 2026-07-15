/*
 * EliminarComprobantePptalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EliminarComprobantePptalControladorUrlEnum {

    URL9796("ELIMINARCOMPROBANTEPPTALCONTROLADORURL9796", "38013"),

    URL14681("ELIMINARCOMPROBANTEPPTALCONTROLADORURL14681", "134001"),

    URL6077("ELIMINARCOMPROBANTEPPTALCONTROLADORURL6077", "38014"),

    URL3111("ELIMINARCOMPROBANTEPPTALCONTROLADORURL3111", "75018"),

    URL3127("ELIMINARCOMPROBANTEPPTALCONTROLADORURL3127", "25017"),

    URL3129("ELIMINARCOMPROBANTEPPTALCONTROLADORURL3129", "72039"),

    URL4682("ELIMINARCOMPROBANTEPPTALCONTROLADORURL4682", "25008"),
    
    URL1914001("ELIMINARCOMPROBANTEPPTALCONTROLADORURL1914001","1914001");

    private final String key;
    private final String value;

    private EliminarComprobantePptalControladorUrlEnum(String key,
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
