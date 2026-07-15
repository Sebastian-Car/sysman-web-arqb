/*
 * ActadesuspensionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ActadesuspensionControladorUrlEnum {

    URL12920("ACTADESUSPENSIONCONTROLADORURL12920", "213024"),

    URL12921("ACTADESUSPENSIONCONTROLADORURL12921", "213026"),

    URL12263("ACTADESUSPENSIONCONTROLADORURL12263", "214022"),
    
    URL12924("ACTADESUSPENSIONCONTROLADORURL12924", "104008"),
    
    URL12925("ACTADESUSPENSIONCONTROLADORURL12925", "364001");


    private final String key;
    private final String value;

    private ActadesuspensionControladorUrlEnum(String key, String value)
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
