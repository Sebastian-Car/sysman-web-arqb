/*
 * DependenciavControladorUrlEnum
 *
 * 1.0
 *
 * 17/01/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum DependenciavControladorUrlEnum {

    URL4130("FRMEVFUNCIONESCONTROLADORURL4130", "62017"),

    URL4131("FRMEVFUNCIONESCONTROLADORURL4131", "62019"),

    ;

    private final String key;
    private final String value;

    private DependenciavControladorUrlEnum(String key, String value)
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
