/*
 * LicenciasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
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
public enum LicenciasControladorUrlEnum {

    URL8112("LICENCIASCONTROLADORURL8112", "627002"),

    URL5784("LICENCIASCONTROLADORURL5784", "471031"),

    URL4698("LICENCIASCONTROLADORURL4698", "7029"),

    URL4234("LICENCIASCONTROLADORURL4234", "471002"),

    URL3847("LICENCIASCONTROLADORURL3847", "627013"),
	
	URL3848("LICENCIASCONTROLADORURL3848", "210160");

    private final String key;
    private final String value;

    private LicenciasControladorUrlEnum(String key, String value)
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
