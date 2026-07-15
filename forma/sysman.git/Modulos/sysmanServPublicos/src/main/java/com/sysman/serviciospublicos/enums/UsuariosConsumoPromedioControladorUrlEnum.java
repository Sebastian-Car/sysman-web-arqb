/*
 * UsuariosConsumoPromedioControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author jguerrero
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al código legacy obtenido con patrones de búsqueda.
 */
public enum UsuariosConsumoPromedioControladorUrlEnum {

    URL7563("USUARIOSCONSUMOPROMEDIOCONTROLADORURL7563",
                    "213059"),

    URL5748("USUARIOSCONSUMOPROMEDIOCONTROLADORURL5748",
                    "227045"),

    URL8348("USUARIOSCONSUMOPROMEDIOCONTROLADORURL8348",
                    "213061"),

    URL6794("USUARIOSCONSUMOPROMEDIOCONTROLADORURL6794",
                    "214031"),

    URL6194("USUARIOSCONSUMOPROMEDIOCONTROLADORURL6194",
                    "227002");

    private final String key;
    private final String value;

    private UsuariosConsumoPromedioControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
