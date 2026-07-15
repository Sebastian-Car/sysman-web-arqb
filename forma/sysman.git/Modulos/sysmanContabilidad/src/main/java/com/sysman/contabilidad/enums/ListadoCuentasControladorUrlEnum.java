/*
 * ListadoCuentasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ListadoCuentasControladorUrlEnum {

    URL3389("LISTADOCUENTASCONTROLADORURL3389", "16008"),

    URL4468("LISTADOCUENTASCONTROLADORURL4468", "16010"),

    URL2855("LISTADOCUENTASCONTROLADORURL2855", "4001");

    private final String key;
    private final String value;

    private ListadoCuentasControladorUrlEnum(String key, String value)
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
