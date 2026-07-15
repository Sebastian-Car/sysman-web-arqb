/*
 * FrmmodificacionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmmodificacionesControladorUrlEnum {

    URL9710("FRMMODIFICACIONESCONTROLADORURL9710", "283001"),

    URL9711("FRMMODIFICACIONESCONTROLADORURL9711", "337001"),

    URL9712("FRMMODIFICACIONESCONTROLADORURL9712", "213106"),

    URL9713("FRMMODIFICACIONESCONTROLADORURL9713", "215023"),

    URL9714("FRMMODIFICACIONESCONTROLADORURL9714", "215025"),

    URL9715("FRMMODIFICACIONESCONTROLADORURL9715", "283002"),

    URL9716("FRMMODIFICACIONESCONTROLADORURL9716", "213115"),

    URL9717("FRMMODIFICACIONESCONTROLADORURL9717", "309010"),
    
    URL9718("FRMMODIFICACIONESCONTROLADORURL9718", "213114"),
    
    URL9719("FRMMODIFICACIONESCONTROLADORURL9719", "213114"),

    ;

    private final String key;
    private final String value;

    private FrmmodificacionesControladorUrlEnum(String key, String value)
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
