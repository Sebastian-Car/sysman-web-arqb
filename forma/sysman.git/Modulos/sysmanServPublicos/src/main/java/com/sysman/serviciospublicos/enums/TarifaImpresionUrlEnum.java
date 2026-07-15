/*
 * TarifaImpresionUrlEnum
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
public enum TarifaImpresionUrlEnum {

    URL8638("TARIFAIMPRESIONURL8638", "4001"),

    URL8961("TARIFAIMPRESIONURL8961", "227002"),

    URL7881("TARIFAIMPRESIONURL7881", "67002");

    private final String key;
    private final String value;

    private TarifaImpresionUrlEnum(String key, String value) {
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
