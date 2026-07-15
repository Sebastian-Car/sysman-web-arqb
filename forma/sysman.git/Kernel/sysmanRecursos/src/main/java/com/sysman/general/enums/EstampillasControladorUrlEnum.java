/*
 * CuentasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum EstampillasControladorUrlEnum {

    URL19121("ESTAMPILLASCONTROLADORURL19121", "199001"), 
    URL19151("ESTAMPILLASCONTROLADORURL19151", "199002"),
    URL19591("ESTAMPILLASCONTROLADORURL19591", "199003"),
    URL19208("ESTAMPILLASCONTROLADORURL19208", "82006"),
    URL19554("ESTAMPILLASCONTROLADORURL19554", "82007"),
    URL19479("ESTAMPILLASCONTROLADORURL19479", "14028");
    

    private final String key;
    private final String value;

    private EstampillasControladorUrlEnum(String key, String value)
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
