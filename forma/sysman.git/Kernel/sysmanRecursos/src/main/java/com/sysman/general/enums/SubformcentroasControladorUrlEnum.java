/*
 * CentroscostosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubformcentroasControladorUrlEnum {

    URL0001("AUXILIARESCONTROLADORURL0001", "119001"),

    URL0002("AUXILIARESCONTROLADORURL0002", "119003"),

    URL0003("AUXILIARESCONTROLADORURL0003", "119005");

    private final String key;
    private final String value;

    private SubformcentroasControladorUrlEnum(String key, String value) {
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
