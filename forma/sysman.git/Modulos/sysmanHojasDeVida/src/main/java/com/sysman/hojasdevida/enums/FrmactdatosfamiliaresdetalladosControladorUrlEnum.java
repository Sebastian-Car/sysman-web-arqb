/*
 * FrmactdatosfamiliaresdetalladosControladorUrlEnum
 *
 * 1.0
 *
 * 26/03/2018
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
public enum FrmactdatosfamiliaresdetalladosControladorUrlEnum {

    URL4130("FRMACTDATOSFAMILIARESDETALLADOSCONTROLADORURL4130", "209001"),

    URL4131("FRMACTDATOSFAMILIARESDETALLADOSCONTROLADORURL4131", "609001");

    private final String key;
    private final String value;

    private FrmactdatosfamiliaresdetalladosControladorUrlEnum(String key,
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
