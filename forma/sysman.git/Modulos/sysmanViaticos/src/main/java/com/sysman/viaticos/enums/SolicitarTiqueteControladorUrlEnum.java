/*
 * SolicitarTiqueteControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SolicitarTiqueteControladorUrlEnum {

    URL17258("SOLICITARTIQUETECONTROLADORURL17258",
                    "5002"),

    URL28547("SOLICITARTIQUETECONTROLADORURL28547",
                    "761010"),

    URL38745("SOLICITARTIQUETECONTROLADORURL38745",
                    "76100R"),

    URL47512("SOLICITARTIQUETECONTROLADORURL47512",
                    "76100U"),

    URL1564("SOLICITARTIQUETECONTROLADORURL1564",
                    "1001"),

    URL4851("SOLICITARTIQUETECONTROLADORURL4851",
                    "2001"),
    
    URL5421("SOLICITARTIQUETECONTROLADORURL5421",
                    "1708001"),
    
    URL1234("SOLICITARTIQUETECONTROLADORURL5421",
                    "104056")

    ;

    private final String key;
    private final String value;

    private SolicitarTiqueteControladorUrlEnum(String key, String value) {
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
