/*
 * LOperacionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LOperacionesControladorUrlEnum {

    URL9672("LOPERACIONESCONTROLADORURL9672", "214070"),

    URL10999("LOPERACIONESCONTROLADORURL10999", "227021"),

    URL11450("LOPERACIONESCONTROLADORURL11450", "227036"),

    URL11451("LOPERACIONESCONTROLADORURL11451", "227007"),

    URL12406("LOPERACIONESCONTROLADORURL12406", "213154"),

    URL12407("LOPERACIONESCONTROLADORURL12407", "213156"),

    URL10157("LOPERACIONESCONTROLADORURL10157", "227001"),

    URL10549("LOPERACIONESCONTROLADORURL10549", "227007"),

    URL11895("LOPERACIONESCONTROLADORURL11895", "362007");

    private final String key;
    private final String value;

    private LOperacionesControladorUrlEnum(String key, String value) {
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
