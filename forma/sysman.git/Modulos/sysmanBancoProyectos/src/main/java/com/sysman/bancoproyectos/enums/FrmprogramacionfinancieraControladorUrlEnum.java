/*
 * FrmprogramacionfinancieraControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmprogramacionfinancieraControladorUrlEnum {

    URL10154("FRMPROGRAMACIONFINANCIERACONTROLADORURL10154",
                    "130017"),

    URL7527("FRMPROGRAMACIONFINANCIERACONTROLADORURL7527",
                    "4046"),

    URL13385("FRMPROGRAMACIONFINANCIERACONTROLADORURL13385",
                    "131029"),

    URL7993("FRMPROGRAMACIONFINANCIERACONTROLADORURL7993",
                    "206006"),

    URL1111("FRMPROGRAMACIONFINANCIERACONTROLADORURL1111",
                    "32025");

    private final String key;
    private final String value;

    private FrmprogramacionfinancieraControladorUrlEnum(String key,
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
