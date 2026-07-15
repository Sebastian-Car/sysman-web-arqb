/*
 * DependenciasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum DependenciasControladorUrlEnum {

    URL13919("DEPENDENCIASCONTROLADORURL13919",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, cDependenciaResponsable,"),

    URL9762("DEPENDENCIASCONTROLADORURL9762",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, cDependenciaResponsable,"),

    URL6679("DEPENDENCIASCONTROLADORURL6679", "102001"),

    URL6046("DEPENDENCIASCONTROLADORURL6046", "20003"),

    URL8188("DEPENDENCIASCONTROLADORURL8188", "61029"),

    URL7319("DEPENDENCIASCONTROLADORURL7319", "61003"),
    
    URL239("DEPENDENCIASCONTROLADORURL329", "1774001"),

    URL4255("DEPENDENCIASCONTROLADORURL4255", "71044"), // "71001"

    URL59031("DEPENDENCIASCONTROLADORURL59031", "59031"),
    
    URL139029("DEPENDENCIASCONTROLADORURL139029", "139029"); 

    private final String key;
    private final String value;

    private DependenciasControladorUrlEnum(String key, String value)
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
