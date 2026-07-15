/*
 * ActualizaSaldoDocuAfectControladorUrlEnum
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
public enum ActualizaSaldoDocuAfectControladorUrlEnum {

    URL7522("ACTUALIZASALDODOCUAFECTCONTROLADORURL7522",
                    "25001"),

    URL6019("ACTUALIZASALDODOCUAFECTCONTROLADORURL6019",
                    "75003"),

    URL4745("ACTUALIZASALDODOCUAFECTCONTROLADORURL4745",
                    "75001"),

    URL4492("ACTUALIZASALDODOCUAFECTCONTROLADORURL4492",
                    "4001");

    private final String key;
    private final String value;

    private ActualizaSaldoDocuAfectControladorUrlEnum(String key,
        String value) {
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
