/*
 * POrdenDeSuministroControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum POrdenDeSuministroControladorUrlEnum {

    URL18002("PORDENDESUMINISTROCONTROLADORURL18002", "61009"),

    URL9024("PORDENDESUMINISTROCONTROLADORURL9024", "112006"),

    URL20478("PORDENDESUMINISTROCONTROLADORURL20478", "112010"),

    URL11750("PORDENDESUMINISTROCONTROLADORURL11750", "102002"),

    URL9994("PORDENDESUMINISTROCONTROLADORURL9994", "62004"),

    URL13505("PORDENDESUMINISTROCONTROLADORURL13505", "4012"),

    URL18401("PORDENDESUMINISTROCONTROLADORURL18401", "114001"),

    URL12244("PORDENDESUMINISTROCONTROLADORURL12244", "114002"),

    URL11617("PORDENDESUMINISTROCONTROLADORURL11617", "61007"),

    URL13165("PORDENDESUMINISTROCONTROLADORURL13165", "23001"),

    URL21552("PORDENDESUMINISTROCONTROLADORURL21552", "23005"),

    URL526("PORDENDESUMINISTROCONTROLADORURL526", "110009"),

    URL10031("PORDENDESUMINISTROCONTROLADORURL10031", "112006"),
    
    URL52002("PORDENDESUMINISTROCONTROLADORURL10031", "52002"),
    
    URL109026("PORDENDESUMINISTROCONTROLADORURL10031", "109026");

    private final String key;
    private final String value;

    private POrdenDeSuministroControladorUrlEnum(String key, String value)
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