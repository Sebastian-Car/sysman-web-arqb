/*
 * AnalisisCarteraUsosControladorUrlEnum
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
public enum AnalisisCarteraUsosControladorUrlEnum {

    URL6969("ANALISISCARTERAUSOSCONTROLADORURL6969",
                    "214028"),
    
    URL1313("ANALISISCARTERAUSOSCONTROLADORURL1313",
                    "310001"),
    
    URL1414("ANALISISCARTERAUSOSCONTROLADORURL1414",
                    "310003"),
    
    
    URL8961("ANALISISCARTERAUSOSCONTROLADORURL8961",
                    "214027"),

    URL9419("ANALISISCARTERAUSOSCONTROLADORURL9419",
                    "242001"),

    URL8569("ANALISISCARTERAUSOSCONTROLADORURL8569",
                    "214026"),

    URL9979("ANALISISCARTERAUSOSCONTROLADORURL9979",
                    "242003");

    private final String key;
    private final String value;

    private AnalisisCarteraUsosControladorUrlEnum(String key, String value) {
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
