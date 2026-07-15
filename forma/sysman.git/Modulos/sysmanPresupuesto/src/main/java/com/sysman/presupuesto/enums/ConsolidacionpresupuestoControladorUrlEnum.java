/*
 * ConsolidacionpresupuestoControladorUrlEnum
 *
 * 1.0
 *
 * 21/03/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ConsolidacionpresupuestoControladorUrlEnum {

    URL001("CONSOLIDACIONPRESUPUESTOCONTROLADORURL001", "59004"),

    URL002("CONSOLIDACIONPRESUPUESTOCONTROLADORURL002", "4001");

    private final String key;
    private final String value;

    private ConsolidacionpresupuestoControladorUrlEnum(String key, String value)
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
