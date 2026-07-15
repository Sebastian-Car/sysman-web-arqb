/*
 * VigenciapolizasControladorUrlEnum
 *
 * 1.0
 *
 * 15/08/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum VigenciapolizasControladorUrlEnum {

    URL3320("VIGENCIAPOLIZASCONTROLADORURL3320", "73012"),

    URL3321("VIGENCIAPOLIZASCONTROLADORURL3321", "73014");

    private final String key;
    private final String value;

    private VigenciapolizasControladorUrlEnum(String key, String value)
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
