/*
 * FacturascorrigeControladorUrlEnum
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
public enum FacturascorrigeControladorUrlEnum {

    URL3664("FACTURASCORRIGECONTROLADORURL3664", "374007"),

    URL3665("FACTURASCORRIGECONTROLADORURL3665", "375001"),

    URL3666("FACTURASCORRIGECONTROLADORURL3666", "367049"),

    ;

    private final String key;
    private final String value;

    private FacturascorrigeControladorUrlEnum(String key, String value)
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
