/*
 * EmbargosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
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
public enum EmbargosControladorUrlEnum {

    URL10343("EMBARGOSCONTROLADORURL10343", "37007"),

    URL12700("EMBARGOSCONTROLADORURL12700", "89002"),

    URL13551("EMBARGOSCONTROLADORURL13551", "151009"),

    URL64754("EMBARGOSCONTROLADORURL64754", "469002"),

    URL82065("EMBARGOSCONTROLADORURL82065", "209001"),

    URL11878("EMBARGOSCONTROLADORURL11878", "459003"),

    URL79742("EMBARGOSCONTROLADORURL79742", "612003"),

    URL15192("EMBARGOSCONTROLADORURL15192", "151015"),

    URL98805("EMBARGOSCONTROLADORURL98805", "210023"),

    URL92848("EMBARGOSCONTROLADORURL92848", "210039");

    private final String key;
    private final String value;

    private EmbargosControladorUrlEnum(String key, String value)
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
