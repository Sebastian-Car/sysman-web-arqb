/*
 * LibroMayoryBalancesCGRControladorUrlEnum
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
public enum LibroMayoryBalancesCGRControladorUrlEnum {

    URL5221("LIBROMAYORYBALANCESCGRCONTROLADORURL5221",
                    "4001"),

    URL6147("LIBROMAYORYBALANCESCGRCONTROLADORURL6147",
                    "29027"),

    URL5647("LIBROMAYORYBALANCESCGRCONTROLADORURL5647",
                    "7006"),

    URL7258("LIBROMAYORYBALANCESCGRCONTROLADORURL7258",
                    "29033");

    private final String key;
    private final String value;

    private LibroMayoryBalancesCGRControladorUrlEnum(String key, String value) {
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
