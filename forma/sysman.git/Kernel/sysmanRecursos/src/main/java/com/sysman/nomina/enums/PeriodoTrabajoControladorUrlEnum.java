/*
 * PeriodoTrabajoControladorUrlEnum
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
public enum PeriodoTrabajoControladorUrlEnum {

    URL4058("PERIODOTRABAJOCONTROLADORURL4058", "537004"),

    URL4735("PERIODOTRABAJOCONTROLADORURL4735", "471008"),

    URL7274("PERIODOTRABAJOCONTROLADORURL7274", "471050"),

    URL5723("PERIODOTRABAJOCONTROLADORURL5723", "471049"),
	
	URL6834("PERIODOTRABAJOCONTROLADORURL6834", "620021");  
	
	private final String key;
    private final String value;

    private PeriodoTrabajoControladorUrlEnum(String key, String value)
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
