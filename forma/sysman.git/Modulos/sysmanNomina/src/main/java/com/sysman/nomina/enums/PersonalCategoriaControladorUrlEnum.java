/*
 * PersonalCategoriaControladorUrlEnum
 *
 * 1.0
 *
 * 18/10/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum PersonalCategoriaControladorUrlEnum {

    URL28520("PERSONALCATEGORIACONTROLADORURL28520", "462002");

    private final String key;
    private final String value;

    private PersonalCategoriaControladorUrlEnum(String key, String value)
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
