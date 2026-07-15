/*
 * LisejecpptalgastosflsControladorUrlEnum
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
public enum LisejecpptalgastosflsControladorUrlEnum {

    URL8754("LISEJECPPTALGASTOSFLSCONTROLADORURL8754", "23019"),

    URL6925("LISEJECPPTALGASTOSFLSCONTROLADORURL6925", "94066"),

    URL5230("LISEJECPPTALGASTOSFLSCONTROLADORURL5230", "7016"),

    URL7986("LISEJECPPTALGASTOSFLSCONTROLADORURL7986", "23010"),

    URL5961("LISEJECPPTALGASTOSFLSCONTROLADORURL5961", "94052"),

    URL4777("LISEJECPPTALGASTOSFLSCONTROLADORURL4777", "4001");

    private final String key;
    private final String value;

    private LisejecpptalgastosflsControladorUrlEnum(String key, String value) {
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
