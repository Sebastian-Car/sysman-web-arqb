/*
 * RegistroReservaPptalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RegistroReservaPptalControladorUrlEnum {

    URL6230("REGISTRORESERVAPPTALCONTROLADORURL6230", "45018"),

    URL5693("REGISTRORESERVAPPTALCONTROLADORURL5693", "4031"),

    URL7424("REGISTRORESERVAPPTALCONTROLADORURL7424", "45020");

    private final String key;
    private final String value;

    private RegistroReservaPptalControladorUrlEnum(String key, String value) {
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
