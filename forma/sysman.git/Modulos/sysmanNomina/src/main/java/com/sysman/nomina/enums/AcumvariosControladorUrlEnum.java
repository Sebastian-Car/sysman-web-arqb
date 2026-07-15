/*
 * AcumvariosControladorUrlEnum
 *
 * 1.0
 *
 * 05/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum AcumvariosControladorUrlEnum {

    // ano 1 y 2
    URL2160("ACUMVARIOSCONTROLADORURL2160", "471006"),

    // mes 1 y 2
    URL2161("ACUMVARIOSCONTROLADORURL2161", "7026"),

    // perido 1 y 2
    URL2162("ACUMVARIOSCONTROLADORURL2162", "471007"),

    // proceso
    URL2163("ACUMVARIOSCONTROLADORURL2163", "537001"),

    // conceptoini
    URL2164("ACUMVARIOSCONTROLADORURL2164", "151001"),

    // conceptofin
    URL2165("ACUMVARIOSCONTROLADORURL2165", "151005"),

    // empleadoini
    URL2166("ACUMVARIOSCONTROLADORURL2166", "210087"),

    // empleadofin
    URL2167("ACUMVARIOSCONTROLADORURL2167", "210014"),

    ;

    private final String key;
    private final String value;

    private AcumvariosControladorUrlEnum(String key, String value)
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
