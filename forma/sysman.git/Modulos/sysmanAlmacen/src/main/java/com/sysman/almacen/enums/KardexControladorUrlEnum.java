/*
 * KardexControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum KardexControladorUrlEnum {

    URL7809("KARDEXCONTROLADORURL7809", "112046"),

    URL11488("KARDEXCONTROLADORURL11488", "112044"),

    URL135009("KARDEXCONTROLADORURLURL7809", "135009"),
    
    URL135014("KARDEXCONTROLADORURLURL7809", "135014");

    private final String key;
    private final String value;

    private KardexControladorUrlEnum(String key, String value) {
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
