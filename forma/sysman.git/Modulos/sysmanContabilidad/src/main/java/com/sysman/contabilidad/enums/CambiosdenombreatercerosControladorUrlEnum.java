/*
 * CambiosdenombreatercerosControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CambiosdenombreatercerosControladorUrlEnum {

    URL3886("CAMBIOSDENOMBREATERCEROSCONTROLADORURL3886",
                    "9000G"),

    URL15318("CAMBIOSDENOMBREATERCEROSCONTROLADORURL15318", "14172"),

    URL5063("CAMBIOSDENOMBREATERCEROSCONTROLADORURL5063", "14001"),

    URL13574("CAMBIOSDENOMBREATERCEROSCONTROLADORURL13574",
                    "90001"),

    URL5871("CAMBIOSDENOMBREATERCEROSCONTROLADORURL5871",
                    "14001"),

    URL8794("CAMBIOSDENOMBREATERCEROSCONTROLADORURL8794",
                    "14035"),

    URL12291("CAMBIOSDENOMBREATERCEROSCONTROLADORURL12291",
                    "76001"),

    URL854("CAMBIOSDENOMBREATERCEROSCONTROLADORURL854",
                    "90002");

    private final String key;
    private final String value;

    private CambiosdenombreatercerosControladorUrlEnum(String key,
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
