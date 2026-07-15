/*
 * ResumenPorDependenciasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ResumenPorDependenciasControladorUrlEnum {

    URL3367("RESUMENAPORTESCONTROLADORURL3367", "471002"),

    URL4125("RESUMENAPORTESCONTROLADORURL4125", "7024"),

    URL4127("RESUMENAPORTESCONTROLADORURL4127", "471009"),

    URL11204("RESUMENPORDEPENDENCIASCONTROLADORURL11204", "471047"),

    URL4123("RESUMENPORDEPENDENCIASCONTROLADORURL4123", "62002");

    private final String key;
    private final String value;

    private ResumenPorDependenciasControladorUrlEnum(String key, String value)
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
