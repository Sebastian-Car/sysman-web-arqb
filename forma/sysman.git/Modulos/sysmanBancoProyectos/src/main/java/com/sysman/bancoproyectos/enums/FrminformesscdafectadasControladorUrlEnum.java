/*
 * FrminformesscdafectadasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrminformesscdafectadasControladorUrlEnum {

    URL5531("FRMINFORMESSCDAFECTADASCONTROLADORURL5531", "32003"),

    URL7458("FRMINFORMESSCDAFECTADASCONTROLADORURL7458", "62011"),

    URL6117("FRMINFORMESSCDAFECTADASCONTROLADORURL6117", "62002"),

    URL4278("FRMINFORMESSCDAFECTADASCONTROLADORURL4278", "32013"),

    URL4949("FRMINFORMESSCDAFECTADASCONTROLADORURL4949", "130015"),

    URL3627("FRMINFORMESSCDAFECTADASCONTROLADORURL3627", "130013");

    private final String key;
    private final String value;

    private FrminformesscdafectadasControladorUrlEnum(String key, String value)
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
