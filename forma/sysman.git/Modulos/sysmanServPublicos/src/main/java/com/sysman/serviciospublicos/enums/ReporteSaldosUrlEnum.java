/*
 * ReporteSaldosUrlEnum
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
public enum ReporteSaldosUrlEnum {

    URL7712("REPORTESALDOSURL7712", "213004"),

    URL8902("REPORTESALDOSURL8902", "213006"),

    URL7067("REPORTESALDOSURL7067", "214029"),

    URL9982("REPORTESALDOSURL9982", "345001"),

    URL10658("REPORTESALDOSURL10658", "345003");

    private final String key;
    private final String value;

    private ReporteSaldosUrlEnum(String key, String value) {
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
