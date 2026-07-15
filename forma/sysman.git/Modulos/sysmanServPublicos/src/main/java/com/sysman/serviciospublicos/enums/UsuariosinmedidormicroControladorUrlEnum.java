/*
 * UsuariosinmedidormicroControladorUrlEnum
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
public enum UsuariosinmedidormicroControladorUrlEnum {

    URL0001("USUARIOSINMEDIDORMICROCONTROLADORURL0001", "366018"),

    URL0002("USUARIOSINMEDIDORMICROCONTROLADORURL0002", "366020"),

    URL4925("USUARIOSINMEDIDORMICROCONTROLADORURL4925", "289020"),

    URL10319("USUARIOSINMEDIDORMICROCONTROLADORURL10319", "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, tablaUpd,");

    private final String key;
    private final String value;

    private UsuariosinmedidormicroControladorUrlEnum(String key, String value)
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
