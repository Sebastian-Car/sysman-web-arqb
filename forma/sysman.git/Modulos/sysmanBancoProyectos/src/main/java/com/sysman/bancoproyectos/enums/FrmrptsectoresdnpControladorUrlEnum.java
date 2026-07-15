/*
 * FrmrptsectoresdnpControladorUrlEnum
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
public enum FrmrptsectoresdnpControladorUrlEnum {

    URL2851("FRMRPTSECTORESDNPCONTROLADORURL2851", "568002"),

    URL3880("FRMRPTSECTORESDNPCONTROLADORURL3880", "568004");
    private final String key;
    private final String value;

    private FrmrptsectoresdnpControladorUrlEnum(String key, String value)
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
