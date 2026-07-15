/*
 * SubirSaldosInicialesControladorUrlEnum
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
public enum SubirSaldosInicialesControladorUrlEnum {

    URL5740("SUBIRSALDOSINICIALESCONTROLADORURL5740", "4001"),

    URL18707("SUBIRSALDOSINICIALESCONTROLADORURL18707", "16043"),

    URL15010("SUBIRSALDOSINICIALESCONTROLADORURL15010", "39017"),

    URL6071("SUBIRSALDOSINICIALESCONTROLADORURL6071", "59006");

    private final String key;
    private final String value;

    private SubirSaldosInicialesControladorUrlEnum(String key, String value) {
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
