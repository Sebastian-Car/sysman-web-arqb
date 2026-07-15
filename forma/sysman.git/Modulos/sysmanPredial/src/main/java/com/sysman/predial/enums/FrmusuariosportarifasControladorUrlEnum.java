/*
 * FrmusuariosportarifasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmusuariosportarifasControladorUrlEnum {

    URL4707("FRMUSUARIOSPORTARIFASCONTROLADORURL4707", "376008"),

    URL3441("FRMUSUARIOSPORTARIFASCONTROLADORURL3441", "4002"),

    URL4038("FRMUSUARIOSPORTARIFASCONTROLADORURL4038", "376006");

    private final String key;
    private final String value;

    private FrmusuariosportarifasControladorUrlEnum(String key, String value)
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
