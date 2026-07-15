/*
 * SolicitudesfinanciamientosControladorUrlEnum
 *
 * 1.0
 *
 * 02/02/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum SolicitudesfinanciamientosControladorUrlEnum {

    URL4130("SOLICITUDESFINANCIAMIENTOSCONTROLADORURL4130", "685048");

    private final String key;
    private final String value;

    private SolicitudesfinanciamientosControladorUrlEnum(String key,
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
