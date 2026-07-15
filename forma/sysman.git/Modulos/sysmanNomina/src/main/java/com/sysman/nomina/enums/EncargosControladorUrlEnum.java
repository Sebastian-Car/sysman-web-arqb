/*
 * EncargosControladorUrlEnum
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
public enum EncargosControladorUrlEnum {

    URL10148("ENCARGOSCONTROLADORURL10148", "462001"),

    URL9626("ENCARGOSCONTROLADORURL9626", "463002"),

    URL9246("ENCARGOSCONTROLADORURL9246", "607008"),
    
    URL7548("ENCARGOSCONTROLADORURL7548", "210078"),

    URL9229("ENCARGOSCONTROLADORURL9229", "471023"),
    
    URL613007("ENCARGOSCONTROLADORURL","613007");
    private final String key;
    private final String value;

    private EncargosControladorUrlEnum(String key, String value)
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
