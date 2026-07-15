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
public enum ParametroAdicionesControladorUrlEnum {

    URL4820("PARAMETROADICIONESCONTROLADORURL4820", "73045"),

    URL4821("PARAMETROADICIONESCONTROLADORURL4821", "73001"),

    URL4822("PARAMETROADICIONESCONTROLADORURL4822", "4001");

    private final String key;
    private final String value;

    private ParametroAdicionesControladorUrlEnum(String key, String value)
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
