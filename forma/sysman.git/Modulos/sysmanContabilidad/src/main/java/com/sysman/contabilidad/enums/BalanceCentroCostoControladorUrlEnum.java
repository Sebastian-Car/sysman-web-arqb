/*
 * BalanceCentroCostoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum BalanceCentroCostoControladorUrlEnum {

    URL5742("BALANCECENTROCOSTOCONTROLADORURL5742",
                    "20009"),

    URL6376("BALANCECENTROCOSTOCONTROLADORURL6376",
                    "20011"),

    URL5173("BALANCECENTROCOSTOCONTROLADORURL5173",
                    "16003"),

    URL4054("BALANCECENTROCOSTOCONTROLADORURL4054",
                    "4001"),

    URL4553("BALANCECENTROCOSTOCONTROLADORURL4553",
                    "16005");

    private final String key;
    private final String value;

    private BalanceCentroCostoControladorUrlEnum(String key, String value) {
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
