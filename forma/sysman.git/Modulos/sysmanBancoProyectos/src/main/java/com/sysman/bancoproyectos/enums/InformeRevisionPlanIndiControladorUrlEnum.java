/*
 * InformeRevisionPlanIndiControladorUrlEnum
 *
 * 1.0
 *
 * 25/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum InformeRevisionPlanIndiControladorUrlEnum {

    URL3410("INFORMEREVISIONPLANINDICONTROLADORURL3410", "4001");

    private final String key;
    private final String value;

    private InformeRevisionPlanIndiControladorUrlEnum(String key, String value)
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
