/*
 * FrmmantenimientoacuerdosControladorUrlEnum
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
public enum FrmmantenimientoacuerdosControladorUrlEnum {

    URL3112("FRMMANTENIMIENTOACUERDOSCONTROLADORURL3112", "367201"),

    URL4278("FRMMANTENIMIENTOACUERDOSCONTROLADORURL4278", "367203");

    private final String key;
    private final String value;

    private FrmmantenimientoacuerdosControladorUrlEnum(String key, String value)
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
