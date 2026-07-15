/*
 * HeaderEnum
 *
 * 1.0
 *
 * 12/08/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.api.clientwso2.util.enums;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar los tipos usados en las
 * cabeceras de peticion.
 */
public enum HeaderEnum {

    ACCEPT("Accept", "application/json"), CONTENT_TYPE("Content-Type",
                    "application/json; charset=utf-8");

    private final String key;
    private final String value;

    private HeaderEnum(String key, String value) {
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
