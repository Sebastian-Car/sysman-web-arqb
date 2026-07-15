/*
 * InformeSiaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InformeSiaControladorUrlEnum {

    URL27878("INFORMESIACONTROLADORURL27878", ""),

    URL24283("INFORMESIACONTROLADORURL24283", ""),

    URL11680("INFORMESIACONTROLADORURL11680", ""),

    URL4660("INFORMESIACONTROLADORURL4660", "4001");

    private final String key;
    private final String value;

    private InformeSiaControladorUrlEnum(String key, String value) {
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
