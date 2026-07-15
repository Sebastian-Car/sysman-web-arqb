/*
 * LibroMayoryBalancesControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum LibroMayoryBalancesControladorUrlEnum {

    URL6250("LIBROMAYORYBALANCESCONTROLADORURL6250", "29033"),
    URL7411("LIBROMAYORYBALANCESCONTROLADORURL7411", "20005"),
    URL8145("LIBROMAYORYBALANCESCONTROLADORURL8145", "20007"),
    URL5213("LIBROMAYORYBALANCESCONTROLADORURL5213", "29027"),
    URL4779("LIBROMAYORYBALANCESCONTROLADORURL4779", "7006"),
    URL4431("LIBROMAYORYBALANCESCONTROLADORURL4431", "4001");

    private final String key;
    private final String value;

    private LibroMayoryBalancesControladorUrlEnum(String key, String value)
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
