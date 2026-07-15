/*
 * PeriodoPptalControladorUrlEnum
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
public enum PeriodoPptalControladorUrlEnum {

    URL4485("PERIODOPPTALCONTROLADORURL4485", "4001"),

    URL4726("PERIODOPPTALCONTROLADORURL4726", "25010"),

    URL207("PERIODOPPTALCONTROLADORURL207", "4011"),

    URL229("PERIODOPPTALCONTROLADORURL229", "7022");

    private final String key;
    private final String value;

    private PeriodoPptalControladorUrlEnum(String key, String value) {
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
