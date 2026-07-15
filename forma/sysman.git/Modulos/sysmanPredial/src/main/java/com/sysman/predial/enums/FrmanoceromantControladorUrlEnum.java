/*
 * FrmanoceromantControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmanoceromantControladorUrlEnum {

    URL3517("FRMANOCEROMANTCONTROLADORURL3517", "4001"),

    URL4035("FRMANOCEROMANTCONTROLADORURL4035", "367013"),

    URL5018("FRMANOCEROMANTCONTROLADORURL5018", "367015");

    private final String key;
    private final String value;

    private FrmanoceromantControladorUrlEnum(String key, String value)
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
