/*
 * ConfigurarcuentasdeudasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum RetencionCierreFiscalControladorUrlEnum {

    URL161("RETENCIONCIERREFISCALCONTROLADORURL161",
                    "16181"),

    URL181("RETENCIONCIERREFISCALCONTROLADORURL181",
                    "4001"),

    URL213("RETENCIONCIERREFISCALCONTROLADORURL213",
                    "1772001"),
    
    URL169("RETENCIONCIERREFISCALCONTROLADORURL169",
                    "16183");

    private final String key;
    private final String value;

    private RetencionCierreFiscalControladorUrlEnum(String key,
        String value)
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
