/*
 * AnoFacturacionControladorUrlEnum
 *
 * 1.0
 *
 * 18/12/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum NatsubnombramientoControladorUrlEnum {

    URL8580("NATSUBNOMBRAMIENTOCONTROLADORURL8580", "700001"),

    URL8581("NATSUBNOMBRAMIENTOCONTROLADORURL8581", "702001"),

    URL8582("NATSUBNOMBRAMIENTOCONTROLADORURL8582", "703001"),

    URL8583("NATSUBNOMBRAMIENTOCONTROLADORURL8583", "463028"),

    URL8584("NATSUBNOMBRAMIENTOCONTROLADORURL8584", "607008"),

    URL8585("NATSUBNOMBRAMIENTOCONTROLADORURL8585", "62002"),

    URL8586("NATSUBNOMBRAMIENTOCONTROLADORURL8586", "462001"),

    URL8587("NATSUBNOMBRAMIENTOCONTROLADORURL8587", "618001")

    ;

    private final String key;
    private final String value;

    private NatsubnombramientoControladorUrlEnum(String key, String value)
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
