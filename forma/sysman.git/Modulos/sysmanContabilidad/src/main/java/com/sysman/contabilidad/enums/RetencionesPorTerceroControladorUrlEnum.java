/*
* LischequesporcobrarControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum RetencionesPorTerceroControladorUrlEnum {

    URL7530("RETENCIONESPORTERCEROCONTROLADOR7530",
                    "12008"),

    URL8420("RETENCIONESPORTERCEROCONTROLADOR8420",
                    "14166"),
    
    URL8418("RETENCIONESPORTERCEROCONTROLADOR8418",
                    "8001"),
    
    URL8421("RETENCIONESPORTERCEROCONTROLADOR8421",
                    "8009"),
    
    URL8419("RETENCIONESPORTERCEROCONTROLADOR8419",
                    "12010"),
    
    URL8425("RETENCIONESPORTERCEROCONTROLADOR8425",
                    "12010")

    
    ;
        	
    private final String key;
    private final String value;
	
    private  RetencionesPorTerceroControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
	
    public String getValue() {
        return value;
    }
}
