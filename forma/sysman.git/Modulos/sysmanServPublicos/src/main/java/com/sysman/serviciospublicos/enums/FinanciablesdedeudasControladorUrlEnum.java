/*
 * FinanciablesdedeudasControladorUrlEnum
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
public enum FinanciablesdedeudasControladorUrlEnum {

    URL0001("FINANCIABLESDEDEUDASCONTROLADORURL0001", "307010"),

    URL0002("FINANCIABLESDEDEUDASCONTROLADORURL0002", "213080"),

    URL0003("FINANCIABLESDEDEUDASCONTROLADORURL0003", "306003"),

    URL12319("FINANCIABLESDEDEUDASCONTROLADORURL12319", "214018"),

    URL13081("FINANCIABLESDEDEUDASCONTROLADORURL13081", "104008"),

    URL13521("FINANCIABLESDEDEUDASCONTROLADORURL13521", "213064");

    private final String key;
    private final String value;

    private FinanciablesdedeudasControladorUrlEnum(String key, String value) {
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
