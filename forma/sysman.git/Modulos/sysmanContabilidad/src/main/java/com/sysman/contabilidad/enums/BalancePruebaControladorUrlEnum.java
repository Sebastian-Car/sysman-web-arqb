/*
 * BalancePruebaControladorUrlEnum
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
public enum BalancePruebaControladorUrlEnum {

    URL5120("BALANCEPRUEBACONTROLADORURL5120",
                    "7005"),

    URL5524("BALANCEPRUEBACONTROLADORURL5524",
                    "7020"),

    URL6041("BALANCEPRUEBACONTROLADORURL6041",
                    "16008"),

    URL6922("BALANCEPRUEBACONTROLADORURL6922",
                    "16010"),

    URL4789("BALANCEPRUEBACONTROLADORURL4789",
                    "4001");

    private final String key;
    private final String value;

    private BalancePruebaControladorUrlEnum(String key, String value) {
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
