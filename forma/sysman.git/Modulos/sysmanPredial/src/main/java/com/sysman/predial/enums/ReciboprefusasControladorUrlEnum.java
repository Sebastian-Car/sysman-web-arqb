/*
 * ReciboprefusasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ReciboprefusasControladorUrlEnum {

    URL4842("RECIBOPREFUSASCONTROLADORURL4842", "385022"),

    URL4843("RECIBOPREFUSASCONTROLADORURL4843", "385019"),

    URL4844("RECIBOPREFUSASCONTROLADORURL4844", "385020"),

    URL4845("RECIBOPREFUSASCONTROLADORURL4845", "396001"),

    URL4846("RECIBOPREFUSASCONTROLADORURL4846", "367186"),

    URL4847("RECIBOPREFUSASCONTROLADORURL4847", "374030"),

    URL4848("RECIBOPREFUSASCONTROLADORURL4848", "400009"),

    URL4849("RECIBOPREFUSASCONTROLADORURL4849", "4030"),

    URL4850("RECIBOPREFUSASCONTROLADORURL4850", "385021"),

    ;

    private final String key;
    private final String value;

    private ReciboprefusasControladorUrlEnum(String key,
        String value)
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
