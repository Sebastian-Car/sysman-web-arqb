/*
 * ParametroAdicionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ParametroNovedadControladorUrlEnum {

    URL5748("PARAMETRONOVEDADCONTROLADORURL5748",
                    "73001"),

    URL5749("PARAMETRONOVEDADCONTROLADORURL5749",
                    "4001"),

    URL6565("PARAMETRONOVEDADCONTROLADORURL6565",
                    "425001");

    private final String key;
    private final String value;

    private ParametroNovedadControladorUrlEnum(String key, String value)
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
