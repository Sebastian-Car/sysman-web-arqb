/*
 * FrmactdatosfamiliaresControlador
 *
 * 1.0
 *
 * 09/09/2016
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
public enum FrmactdatosfamiliaresControladorUrlEnum {

    URL001("FRMACTDATOSFAMILIARESCONTROLADORURL001", "723011"),

    URL002("FRMACTDATOSFAMILIARESCONTROLADORURL002", "209001"),

    URL003("FRMACTDATOSFAMILIARESCONTROLADORURL003", "609001"),

    URL004("FRMACTDATOSFAMILIARESCONTROLADORURL004", "209001"),

    URL005("FRMACTDATOSFAMILIARESCONTROLADORURL005", "956001"),

    URL006("FRMACTDATOSFAMILIARESCONTROLADORURL006", "98900C"),

    URL007("FRMACTDATOSFAMILIARESCONTROLADORURL007", "98900D"),

    ;

    private final String key;
    private final String value;

    private FrmactdatosfamiliaresControladorUrlEnum(String key, String value)
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
