/*
 * BancosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImpresionReportesControladorUrlEnum {

    URL2361("IMPRESIONREPORTESCONTROLADORURL2361", "1055003"),

    URL2362("IMPRESIONREPORTESCONTROLADORURL2362", "209003"),

    URL85472("IMPRESIONREPORTESCONTROLADORURL85472","1055005");

    private final String key;
    private final String value;

    private ImpresionReportesControladorUrlEnum(String key, String value) {
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
